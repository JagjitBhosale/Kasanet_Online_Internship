package com.kasnet.dao;

import com.kasnet.model.VideoAccessLog;
import com.kasnet.util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for VideoAccessLog entries.
 * Records every video access request for audit and rate-limiting purposes.
 */
public class VideoAccessLogDAO {

    /**
     * Inserts an audit log entry when a student accesses a video.
     *
     * @param userId    the student's user ID
     * @param lectureId the lecture (video) being accessed
     * @param ipAddress the client IP address
     * @param userAgent the client User-Agent string
     * @return true if the log was persisted
     */
    public boolean logAccess(int userId, int lectureId, String ipAddress, String userAgent) {
        String sql = "INSERT INTO video_access_logs (user_id, lecture_id, ip_address, user_agent, created_at) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, lectureId);
            ps.setString(3, ipAddress);
            ps.setString(4, userAgent != null && userAgent.length() > 500
                    ? userAgent.substring(0, 500) : userAgent);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Counts recent video requests from a specific IP within N minutes (rate limiting).
     */
    public int countRecentAccessesByIp(String ipAddress, int minutes) {
        String sql = "SELECT COUNT(*) FROM video_access_logs " +
                     "WHERE ip_address = ? AND created_at >= NOW() - INTERVAL ? MINUTE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ipAddress);
            ps.setInt(2, minutes);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns the total number of video views by a specific user.
     */
    public int countByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM video_access_logs WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
