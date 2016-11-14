package DataStructureBuilder;

import ParserConfigurator.Configurator;
import ParserConfigurator.Node;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Spark on 14/11/2016.
 */
public class Generator {

    // Session
    public SparkSession sparkSession;
    // Tables
    public Dataset<Row> employees;
    public Dataset<Row> salaries;
    public Dataset<Row> departments;
    public Dataset<Row> titles;
    public Dataset<Row> dept_emp;
    public Dataset<Row> dept_manager;
    // System nodes
    public List<Node> nodes;

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

        // Generate configuration data
        Configurator conf = new Configurator();
        nodes = conf.parse("src/main/resources/DB_config.json");
    }
}
