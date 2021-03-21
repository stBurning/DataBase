import java.sql.*

/** @Домашка за себя и за Сашку:
 *  Разработать структуру БД для сохраниения информации о студентах:
 *  - ФИО
 *  - Группа
 *  - Год поступления
 *  - Направление
 *  - Квалицикация
 *
 *  Для каждого направления нужна информация о дисциплинах(учебный план),
 *  кол-во часов по семестрам,
 *  форма контроля. Учебный план соответствует году и направлению
 *  Студент - оценки, с какого раза сдано ы
 * **/
class DBController(
    private val dbName: String,
    private val address: String = "localhost",
    private val port: Int = 3306,
    private val user: String = "root",
    private val password: String = "root") {

    private var connection: Connection? = null



    init {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://$address:$port/$dbName?serverTimezone=UTC", user, password)
            val s = connection?.createStatement()
            //////////////////////////////////////////////////////////////

//            try { s?.execute() }
//            catch (e: SQLSyntaxErrorException){
//                println("WARNING! ${e.message}")
//            }
            s?.execute("delete from `test`")
            (1..10).forEach{
                val ps = connection?.prepareStatement("insert into `test` (text_field, int_field) values (?, ?)")
                ps?.setString(1, "QUE~OTA")
                ps?.setInt(2, it)
                val rows = ps?.executeUpdate()
                println(rows)
            }
        }catch (ex: SQLException){
            println("WARNING! ${ex.message}")
        }




        //////////////////////////////////////////////////////////////
    }

}