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
        dropAllTables()
        createDataBaseFromDump("src/main/kotlin/students.sql")
        fillTableFromCSV("cathedras", "data/cathedras.csv")
        fillTableFromCSV("disciplines", "data/disciplines.csv")
        fillTableFromCSV("specializations", "data/specializations.csv")
        fillTableFromCSV("academic_plans", "data/academic_plans.csv")
        fillTableFromCSV("groups", "data/groups.csv")
        fillTableFromCSV("disciplines_plans", "data/disciplines_plans.csv")
        fillTableFromCSV("students", "data/students.csv")
        fillTableFromCSV("performance", "data/performance.csv")
    }

    private fun dropAllTables(){
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

    /**Функция, создающая таблицы в базе данных на основе SQL-дампа
     * @param path путь до SQL-дампа*/
    private fun createDataBaseFromDump(path: String){
        println("Создание структуры базы данных из дампа...")
        try {
            val s = connection?.createStatement()
            var query = ""
            File(path).forEachLine {
                if(!it.startsWith("--") && it.isNotEmpty()){
                        query += it;
                    if (it.endsWith(';')) {
                        s?.addBatch(query)
                        query = ""
                    }
                }
            }
            s?.executeBatch()
            println("Структура базы данных успешно создана.")
        }catch (e: SQLException){ println(e.message)
        }catch (e: Exception){ println(e.message)}
    }

    /**Функция для заполнения таблицы из CSV - файла
     * @param table название таблицы в базе данных
     * @param path путь до источника данных (CSV - файла)
     * TODO Добавить Exception для ощибок с чтением файла*/
    private fun fillTableFromCSV(table: String, path: String){

        println("Заполнение таблицы $table из файла $path")
        val s = connection?.createStatement()
        try {
            var requestTemplate = "INSERT INTO `${table}` "
            val dataBufferedReader = File(path).bufferedReader()
            val columns = dataBufferedReader.readLine()
                .split(',')
                .toString()
            requestTemplate += "(${columns.substring(1, columns.length - 1)}) VALUES "

            while (dataBufferedReader.ready()){
                var request = "$requestTemplate("
                val data = dataBufferedReader.readLine().split(',')
                data.forEachIndexed{i, column ->
                    request += "\"$column\""
                    if (i < data.size - 1) request += ','
                }
                request += ')'
                s?.addBatch(request)
            }
            s?.executeBatch()
            s?.clearBatch()

        }catch (e: SQLException){ println(e)}

    }

}