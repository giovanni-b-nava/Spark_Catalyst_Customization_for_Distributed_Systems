package DataConfigBuilder;

import ConfigurationParser.Configurator;
import ConfigurationParser.Provider;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // List of tables (correspondent list of table names)
    public List<Dataset<Row>> tables = new ArrayList<>();
    public List<String> tableNames = new ArrayList<>();
    // System providers
    public List<Provider> providers;
    // Map of the dimension of the tables
    public Map<String, Long> tuples = new HashMap<>();

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

        // Fill the table lists
        tables.add(employees);
        tableNames.add("employees");
        tables.add(salaries);
        tableNames.add("salaries");
        tables.add(departments);
        tableNames.add("departments");
        tables.add(titles);
        tableNames.add("titles");
        tables.add(dept_emp);
        tableNames.add("dept_emp");
        tables.add(dept_manager);
        tableNames.add("dept_manager");

        // Fill the tables dimension
        tuples.put("employees", employees.count());
        tuples.put("salaries", salaries.count());
        tuples.put("departments", departments.count());
        tuples.put("titles", titles.count());
        tuples.put("dept_emp", dept_emp.count());
        tuples.put("dept_manager", dept_manager.count());

        // Generate configuration data
        Configurator conf = new Configurator();
        providers = conf.parse("src/main/resources/DB_config.json");
    }
}
