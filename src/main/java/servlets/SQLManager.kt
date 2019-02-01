package servlets

import model.Post
import org.apache.tomcat.jdbc.pool.DataSource
import java.sql.CallableStatement
import java.sql.Connection
import java.sql.Statement
import java.util.*
import kotlin.collections.ArrayList

interface SQLManager<T> {
    fun createConnection(): Connection {
        val properties = Properties()
        val dataSource = DataSource()

        dataSource.setDriverClassName("com.mysql.jdbc.Driver")
        dataSource.setUrl("jdbc:mysql://localhost:3306/mitello")
        dataSource.setUsername("root")
        dataSource.setPassword("En7j6pur8v")
        return dataSource.getConnection()
    }
    fun getRows(): Array<T>
    fun getRow(id: Int): T
    fun createTable()
    fun deleteRow(id: Int)
    fun createRow(value: T)
    fun dropTable();
    fun editRow(id: Int, value: T)
    fun closeConnection()

}
class PostsManager(): SQLManager<Post> {
    override fun closeConnection() {
        conn.close();
    }

    private val conn = createConnection()

    override fun getRows(): Array<Post> {
        conn.createStatement().apply {
            execute("select * from Posts")
            resultSet.apply {
                val posts = ArrayList<Post>()
                while (next()) {
                    posts.add(Post(getString("username"), getString("content"), getInt("id")))
                }
                return posts.toTypedArray()
            }
        }
    }

    override fun getRow(id: Int): Post {
        conn.prepareStatement("select * from Posts where id=?").apply {
            setObject(1, id)
            execute()
            resultSet.apply {
                next()
                return Post(getString("username"), getString("content"), getInt("id"))
            }
        }
    }

    override fun createTable() {
        conn.createStatement().execute("create table if not exists Posts (id int PRIMARY KEY auto_increment, username varchar(25), content varchar(30));")
    }

    override fun deleteRow(id: Int) {
        conn.prepareStatement("delete from Posts where id=?").apply {
            setObject(1, id)
            execute()
        }
    }

    override fun createRow(value: Post) {
        conn.prepareStatement("insert into Posts (username, content) value (?, ?)").apply {
            setObject(1, value.username)
            setObject(2, value.content)
            execute()
        }
    }

    override fun dropTable() {
        conn.createStatement().execute("drop table if exists Posts")
    }

    override fun editRow(id: Int, value: Post) {
        conn.prepareStatement("update Posts set content = ?, username = ? where id= ?").apply {
            setObject(1, value.content)
            setObject(2, value.username)
            setObject(3, value.id)
            execute()
        }
    }

}