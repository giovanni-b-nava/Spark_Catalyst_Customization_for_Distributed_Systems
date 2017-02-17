package DataConfigBuilder;

import ConfigurationParser.Configurator;
import ConfigurationParser.Provider;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DataBuilder
{

    // Spark Session
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

    // Providers
    public List<Provider> providers;

    // Builder is a singleton
    private static DataBuilder builder = null;

    private DataBuilder() {}

    public static synchronized DataBuilder getDataBuilder()
    {
        if (builder == null)
        {
            builder = new DataBuilder();
        }
        return builder;
    }

    // Builds all variable and data necessary
    public void buildData() throws FileNotFoundException
    {
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

        // Generate configuration data
        Configurator conf = new Configurator();
        providers = conf.parse("src/main/resources/DB_config.json");
    }
}
