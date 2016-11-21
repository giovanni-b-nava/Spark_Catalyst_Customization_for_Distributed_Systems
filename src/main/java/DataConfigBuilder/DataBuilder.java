package DataConfigBuilder;

import ParserConfigurator.Configurator;
import ParserConfigurator.Node;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spark on 14/11/2016.
 */
public class DataBuilder {

    // Session
    public SparkSession sparkSession;
    // Tables
    public Dataset<Row> employees;
    public Dataset<Row> salaries;
    public Dataset<Row> departments;
    public Dataset<Row> titles;
    public Dataset<Row> dept_emp;
    public Dataset<Row> dept_manager;
    // List of tables
    public List<Dataset<Row>> tables = new ArrayList<>();
    // System nodes
    public List<Node> nodes;

    // Builder is a singleton
    private static DataBuilder builder = null;

    private DataBuilder() {}

    public static synchronized DataBuilder getDataBuilder() {
        if (builder == null) {
            builder = new DataBuilder();
        }
        return builder;
    }

    // Builds all variable and data necessary
    public void buildData() throws FileNotFoundException {

        // Create entry point
        sparkSession = org.apache.spark.sql.SparkSession
                .builder()
                .master("local")
                .appName("Java Spark SQL Example")
                .getOrCreate();

        // Generate tables datasets
        employees = sparkSession.read().option("header","true").csv("src/main/resources/employees.csv");
        salaries = sparkSession.read().option("header","true").csv("src/main/resources/salaries.csv");
        departments = sparkSession.read().option("header","true").csv("src/main/resources/departments.csv");
        titles = sparkSession.read().option("header","true").csv("src/main/resources/titles.csv");
        dept_emp = sparkSession.read().option("header","true").csv("src/main/resources/dept_emp.csv");
        dept_manager = sparkSession.read().option("header","true").csv("src/main/resources/dept_manager.csv");

        // Fill the table list
        tables.add(employees);
        tables.add(salaries);
        tables.add(departments);
        tables.add(titles);
        tables.add(dept_emp);
        tables.add(dept_manager);

        // Generate configuration data
        Configurator conf = new Configurator();
        nodes = conf.parse("src/main/resources/DB_config.json");
    }
}
