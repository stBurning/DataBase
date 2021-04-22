import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class DBHelper(
    private val dbName: String,
    private val address: String = "localhost",
    private val port: Int = 3306,
    private val user: String = "root",
    private val password: String = "root"){
    private var connection: Connection? = null

    init {
        connection = DriverManager.getConnection(
            "jdbc:mysql://$address:$port/$dbName?serverTimezone=UTC", user, password
        )

    }

    fun dropAllTables(){
        println("Удаление всех таблиц в базе данных...")
        val s = connection?.createStatement()
        s?.execute("DROP TABLE if exists `performance`")
        s?.execute("DROP TABLE if exists `students`")
        s?.execute("DROP TABLE if exists `groups`")
        s?.execute("DROP TABLE if exists `disciplines_plans`")
        s?.execute("DROP TABLE if exists `academic_plans`")
        s?.execute("DROP TABLE if exists `disciplines`")
        s?.execute("DROP TABLE if exists `cathedras`")
        s?.execute("DROP TABLE if exists `specializations`")
        println("Все таблицы удалены.")

    }

    fun getStudentResults(fname: String, lname: String, mname: String, birthdate: String) {
        val s = connection?.createStatement()
        val idQuery = "SELECT `id` FROM `students` WHERE students.first_name = " +
                "\"${fname}\" AND students.last_name = \"${lname}\" AND students.mid_name = \"${mname}\" AND students.birthday = \"${birthdate}\""

        val getId = s?.executeQuery(idQuery)
        var id = 0
        if (getId?.next() == true) id = getId.getInt("id")

        val query = "SELECT `st_id`, disciplines_plans.discipline_id, disciplines.name ,`qq`.`hrs` ,FLOOR(`qq`.`hrs`/36) as `zach_ed`,`qq`.`sem_num`, performance.score,\n" +
                "(CASE \n" +
                " \tWHEN `reporting_form` = \"экзамен\" AND `score` > 86 AND `attempt` = 1 THEN \"5\" \n" +
                " \tWHEN `reporting_form` = \"экзамен\" AND `score` > 70 AND `attempt` = 1 THEN \"4\"\n" +
                " \tWHEN `reporting_form` = \"экзамен\" AND `score` > 55 AND `attempt` = 1 THEN \"3\"\n" +
                " \tWHEN `reporting_form` = \"зачет\" AND `score` > 55 AND `attempt` = 1 THEN \"+\"\n" +
                " ELSE \"-\" END) as `mark`, performance.attempt, disciplines_plans.reporting_form FROM  \n" +
                "(SELECT performance.student_id as `st_id`, discipline_id, MAX(disciplines_plans.semester_number) as `sem_num`,\n" +
                "                SUM(disciplines_plans.hours) as `hrs` FROM `performance` JOIN `disciplines_plans` ON `performance`.`student_id` = $id\n" +
                "                AND `performance`.`disciplines_plan_id` = `disciplines_plans`.`id` GROUP BY disciplines_plans.discipline_id) as `qq` \n" +
                "                JOIN (performance \n" +
                "                      JOIN (disciplines_plans JOIN disciplines ON disciplines.id = disciplines_plans.discipline_id) \n" +
                "                      ON performance.disciplines_plan_id = disciplines_plans.id) \n" +
                "                ON performance.student_id = `qq`.`st_id` AND\n" +
                "                disciplines_plans.discipline_id = `qq`.`discipline_id` AND `qq`.`sem_num` = disciplines_plans.semester_number"



        val rs = s?.executeQuery(query)

        val format = "| %-7s | %-38s | %12s | %3s | %10s | %5s | %6s | %-7s |%n"
        println("Студент: $fname $lname $mname")
        System.out.format("+---------+----------------------------------------+--------------+-----+------------+-------+--------+---------|%n")
        System.out.format("| Код     | Название дисциплины                    | Кол-во часов | з/е | № семестра | Баллы | Оценка | Формат  |%n")
        System.out.format("+---------+----------------------------------------+--------------+-----+------------+-------+--------+---------|%n")
        while (rs?.next() == true) {
            System.out.format(format,
                rs.getString("discipline_id"),
                rs.getString("name"),
                rs.getString("hrs"),
                rs.getString("zach_ed"),
                rs.getString("sem_num"),
                rs.getString("score"),
                rs.getString("mark"),
                rs.getString("reporting_form")
            )
        }
        System.out.format("+---------+----------------------------------------+--------------+-----+------------+-------+--------+---------|%n")
    }

        fun printScholarship() {
            println("Стипендии по результатам последней сессии")
            val s = connection?.createStatement()
            val rs = s?.executeQuery(
                "SELECT `id`,`last_name`,`first_name`,`mid_name`,`group_id`,`scholarship` FROM students JOIN (SELECT `student_id`, \n" +
                        " (CASE \n" +
                        "\t WHEN MIN(`result`) = 5 THEN 3100 \n" +
                        "\t WHEN MIN(`result`) = 4 THEN 2100\n" +
                        " \tELSE 0\n" +
                        " END) AS `scholarship`\n" +
                        "\n" +
                        "FROM (SELECT `student_id`, `score`, `attempt`, `reporting_form`,\n" +
                        " (CASE \n" +
                        "  \tWHEN `reporting_form` = \"экзамен\" AND `score` > 86 AND `attempt` = 1 THEN 5 \n" +
                        "  \tWHEN `reporting_form` = \"экзамен\" AND `score` > 70 AND `attempt` = 1 THEN 4\n" +
                        "  \tWHEN `reporting_form` = \"экзамен\" AND `score` > 55 AND `attempt` = 1 THEN 3\n" +
                        "  \tWHEN `reporting_form` = \"зачет\" AND `score` > 55 AND `attempt` = 1 THEN 5\n" +
                        "  \tELSE 0\n" +
                        "  END) AS `result` \n" +
                        " FROM performance JOIN disciplines_plans JOIN \n" +
                        " \t(SELECT `students`.`id`, current_semester FROM `students` JOIN\n" +
                        "    \t(SELECT year, groups.id, 2*(YEAR(NOW()) - year) - (CASE WHEN MONTH(NOW()) > 6 OR MONTH(NOW()) = 1 THEN 1 ELSE 0 END) AS `current_semester` \n" +
                        "\t\t\tFROM `groups` JOIN `academic_plans` ON `groups`.`academic_plan_id` = `academic_plans`.`id`) AS `group_cs` \n" +
                        "                            ON `students`.`group_id` = `group_cs`.`id`) AS `swcs` ON performance.student_id = swcs.id \n" +
                        "                            AND disciplines_plans.id = performance.disciplines_plan_id \n" +
                        "                            AND swcs.current_semester = disciplines_plans.semester_number + 1) AS `rank` GROUP BY `student_id`) AS `swsh` ON `students`.`id` = swsh.student_id ORDER BY group_id, last_name,first_name, mid_name"
            )


            while (rs?.next() == true) {
                println(
                    "ID: ${rs.getInt("id")}\t" +
                            "${rs.getString("last_name")}\t" +
                            "${rs.getString("first_name")}\t" +
                            "${rs.getString("mid_name")}\t" +
                            "${rs.getString("group_id")}\t" +
                            "${rs.getInt("scholarship")}\t"
                )
            }


        }

        /**Функция, создающая таблицы в базе данных на основе SQL-дампа
         * @param path путь до SQL-дампа*/
        fun createDataBaseFromDump(path: String) {
            println("Создание структуры базы данных из дампа...")
            try {
                val s = connection?.createStatement()
                var query = ""
                File(path).forEachLine {
                    if (!it.startsWith("--") && it.isNotEmpty()) {
                        query += it;
                        if (it.endsWith(';')) {
                            s?.addBatch(query)
                            query = ""
                        }
                    }
                }
                s?.executeBatch()
                println("Структура базы данных успешно создана.")
            } catch (e: SQLException) {
                println(e.message)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        /**Функция для заполнения таблицы из CSV - файла
         * @param table название таблицы в базе данных
         * @param path путь до источника данных (CSV - файла)
         * TODO Добавить Exception для ощибок с чтением файла*/
        fun fillTableFromCSV(table: String, path: String) {

            println("Заполнение таблицы $table из файла $path")
            val s = connection?.createStatement()
            try {
                var requestTemplate = "INSERT INTO `${table}` "
                val dataBufferedReader = File(path).bufferedReader()
                val columns = dataBufferedReader.readLine()
                    .split(',')
                    .toString()
                requestTemplate += "(${columns.substring(1, columns.length - 1)}) VALUES "

                while (dataBufferedReader.ready()) {
                    var request = "$requestTemplate("
                    val data = dataBufferedReader.readLine().split(',')
                    data.forEachIndexed { i, column ->
                        request += "\"$column\""
                        if (i < data.size - 1) request += ','
                    }
                    request += ')'
                    s?.addBatch(request)
                }
                s?.executeBatch()
                s?.clearBatch()

            } catch (e: SQLException) {
                println(e)
            }

        }

}