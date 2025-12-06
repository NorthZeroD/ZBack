package io.github.northzerod.zBack

import io.github.northzerod.zBack.ZBack.Companion.log
import java.sql.Connection
import java.sql.DriverManager

class DatabaseManager(val url: String) {
    lateinit var connection: Connection

    init {
        connect()
        create()
    }

    fun connect() {
        try {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection(url)
            log.info("Successfully connect to database.db")
        } catch (e: Exception) {
            log.warning("Cannot connect to database.db")
            throw e
        }
    }

    fun create(
        sql: String = """
            CREATE TABLE IF NOT EXISTS last_death (
            	uuid TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
            	dimension TEXT NOT NULL,
            	x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL,
                is_used_back INTEGER NOT NULL
            );
        """.trimIndent()
    ) {
        try {
            val stmt = connection.createStatement()
            stmt.execute(sql)
        } catch (e: Exception) {
            log.warning("Exception occurred when executing SQL:\n$sql")
            throw e
        }
    }

    fun update(
        playerData: PlayerData,
        sql: String = """
                INSERT INTO last_death (uuid, name, dimension, x, y, z, yaw, pitch, is_used_back) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    uuid = excluded.uuid,
                    name = excluded.name,
                    dimension = excluded.dimension,
                    x = excluded.x,
                    y = excluded.y,
                    z = excluded.z,
                    yaw = excluded.yaw,
                    pitch = excluded.pitch,
                    is_used_back = excluded.is_used_back;
                """.trimIndent()
    ) {
        try {
            val pstmt = connection.prepareStatement(sql)
            pstmt.setString(1, playerData.uuid)
            pstmt.setString(2, playerData.name)
            pstmt.setString(3, playerData.dimension)
            pstmt.setDouble(4, playerData.x)
            pstmt.setDouble(5, playerData.y)
            pstmt.setDouble(6, playerData.z)
            pstmt.setFloat(7, playerData.yaw)
            pstmt.setFloat(8, playerData.pitch)
            pstmt.setBoolean(9, playerData.isUsedBack)
            pstmt.executeUpdate()
        } catch (e: Exception) {
            log.warning("Exception occurred when executing SQL:\n$sql")
            throw e
        }
    }

    fun close() {
        try {
            connection.close()
        } catch (e: Exception) {
            log.warning("Exception occurred when closing database connection")
            throw e
        }
    }

    fun query(
        uuid: String,
        sql: String = "SELECT * FROM last_death WHERE uuid = ?;"
    ): PlayerData? {
        try {
            val pstmt = connection.prepareStatement(sql)
            pstmt.setString(1, uuid)
            val rs = pstmt.executeQuery()
            if (rs.next()) {
                return PlayerData(
                    uuid,
                    rs.getString("name"),
                    rs.getString("dimension"),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z"),
                    rs.getFloat("yaw"),
                    rs.getFloat("pitch"),
                    rs.getBoolean("is_used_back")
                )
            }
            return null
        } catch (e: Exception) {
            log.warning("Exception occurred when executing SQL:\n$sql")
            throw e
        }
    }
}
