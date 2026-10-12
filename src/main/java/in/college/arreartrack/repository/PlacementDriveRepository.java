package in.college.arreartrack.repository;

import in.college.arreartrack.model.PlacementDrive;
import java.sql.SQLException;
import java.util.List;

public interface PlacementDriveRepository {
    List<PlacementDrive> findAll() throws SQLException;
    void save(PlacementDrive drive) throws SQLException;
    void delete(long driveId) throws SQLException;
}
