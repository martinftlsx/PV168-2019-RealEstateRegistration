package cz.muni.fi.pv168;

import com.zaxxer.hikari.HikariDataSource;
import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.managers.OwnerManager;
import cz.muni.fi.pv168.managers.OwnerManagerImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @author Martin Podhora
 */
public class Main {
    public static DataSource createDataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        //load connection properties from a file
        Properties p = new Properties();
        try {
            p.load(Main.class.getResourceAsStream("/myconf.properties"));
        } catch (IOException _) {
        }

        //set connection
        dataSource.setDriverClassName(p.getProperty("jdbc.driver"));
        dataSource.setJdbcUrl(p.getProperty("jdbc.url"));
        dataSource.setUsername(p.getProperty("jdbc.user"));
        dataSource.setPassword(p.getProperty("jdbc.password"));

        //populate db with tables and data
        new ResourceDatabasePopulator(
                new ClassPathResource("createTables.sql"),
                new ClassPathResource("fillOwnerTable.sql"))
                .execute(dataSource);
        return dataSource;
    }

    public static void main(String[] args) throws SQLException, IOException {
        createDataSource();
    }
}
