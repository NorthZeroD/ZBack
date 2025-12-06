package io.github.northzerod.zBack

import io.github.northzerod.zBack.ZBack.Companion.plg
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
            plg.logger.info("Successfully connect to database.db")
        } catch (e: Exception) {
            plg.logger.warning("Cannot connect to database.db")
            throw e
        }
    }

    fun create(
        sql: String = """
            CREATE TABLE IF NOT EXISTS last_death (
            	uuid TEXT NOT NULL PRIMARY KEY,
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
            plg.logger.warning("Exception occurred when executing SQL:\n$sql")
            throw e
        }
    }

    fun update(
        playerData: PlayerData,
        sql: String = """
                INSERT INTO last_death (uuid, dimension, x, y, z, yaw, pitch, is_used_back) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    uuid = excluded.uuid,
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
            pstmt.setString(2, playerData.dimension)
            pstmt.setDouble(3, playerData.x)
            pstmt.setDouble(4, playerData.y)
            pstmt.setDouble(5, playerData.z)
            pstmt.setFloat(6, playerData.yaw)
            pstmt.setFloat(7, playerData.pitch)
            pstmt.setBoolean(8, playerData.isUsedBack)
            pstmt.executeUpdate()
        } catch (e: Exception) {
            plg.logger.warning("Exception occurred when executing SQL:\n$sql")
            throw e
        }
    }

    fun close() {
        try {
            connection.close()
        } catch (e: Exception) {
            plg.logger.warning("Exception occurred when closing database connection")
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
            plg.logger.warning("Exception occurred when executing SQL:\n$sql")
            throw e
        }
    }
}
