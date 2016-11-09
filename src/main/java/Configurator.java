import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Spark on 09/11/2016.
 */
public class Configurator {

    public List<Node> nodes;

    public void Builder() {

        // Encryption values for the client node
        Map<String, Double> ecl1 = new HashMap<>();
        ecl1.put("aes", 500.0);

        // Encryption values for the storage node
        Map<String, Double> ess1 = new HashMap<>();
        ecl1.put("aes", 500.0);

        // Encryption values for the computational node
        Map<String, Double> ecs1 = new HashMap<>();
        ecl1.put("aes", 500.0);

        // Costs for the client node
        Map<String, Double> ccl1 = new HashMap<>();
        ccl1.put("cpu", 0.0006);
        ccl1.put("in", 0.0);
        ccl1.put("in", 0.0);

        // Costs for the storage node
        Map<String, Double> css1 = new HashMap<>();
        css1.put("cpu", 0.0002);
        css1.put("in", 0.00001);
        css1.put("in", 0.00001);

        // Costs for the computational node
        Map<String, Double> ccs1 = new HashMap<>();
        ccs1.put("cpu", 0.00006);
        ccs1.put("in", 0.00001);
        ccs1.put("in", 0.00001);

        // Lists of transfer links for the client node
        Map<String, Double> cl1_links = new HashMap<>();
        cl1_links.put("SS1", 100.0);
        cl1_links.put("CS1", 100.0);

        // Lists of transfer links for the storage node
        Map<String, Double> ss1_links = new HashMap<>();
        ss1_links.put("CL1", 100.0);
        ss1_links.put("CS1", 10000.0);

        // Lists of transfer links for the computational node
        Map<String, Double> cs1_links = new HashMap<>();
        cs1_links.put("CL1", 100.0);
        cs1_links.put("SS1", 10000.0);

        // Lists of attributes for the client node
        List<String> plain_employees_CL1 = new ArrayList<String>();
        List<String> encr_employees_CL1 = new ArrayList<String>();
        List<String> plain_departments_CL1 = new ArrayList<String>();
        List<String> encr_departments_CL1 = new ArrayList<String>();
        List<String> plain_dept_emp_CL1 = new ArrayList<String>();
        List<String> encr_dept_emp_CL1 = new ArrayList<String>();
        List<String> plain_dept_manager_CL1 = new ArrayList<String>();
        List<String> encr_dept_manager_CL1 = new ArrayList<String>();
        List<String> plain_salaries_CL1 = new ArrayList<String>();
        List<String> encr_salaries_CL1 = new ArrayList<String>();
        List<String> plain_titles_CL1 = new ArrayList<String>();
        List<String> encr_titles_CL1 = new ArrayList<String>();

        // Population of the client tables
        plain_employees_CL1.add("emp_no");
        plain_employees_CL1.add("birth_date");
        plain_employees_CL1.add("first_name");
        plain_employees_CL1.add("last_name");
        plain_employees_CL1.add("gender");
        plain_employees_CL1.add("hire_date");

        plain_departments_CL1.add("dept_no");
        plain_departments_CL1.add("dept_name");

        plain_dept_emp_CL1.add("emp_no");
        plain_dept_emp_CL1.add("dept_no");
        plain_dept_emp_CL1.add("from_date");
        plain_dept_emp_CL1.add("to_date");

        plain_dept_manager_CL1.add("emp_no");
        plain_dept_manager_CL1.add("dept_no");
        plain_dept_manager_CL1.add("from_date");
        plain_dept_manager_CL1.add("to_date");

        plain_salaries_CL1.add("emp_no");
        plain_salaries_CL1.add("salary");
        plain_salaries_CL1.add("from_date");
        plain_salaries_CL1.add("to_date");

        plain_titles_CL1.add("emp_no");
        plain_titles_CL1.add("title");
        plain_titles_CL1.add("from_date");
        plain_titles_CL1.add("to_date");

        // Lists of attributes for the storage node
        List<String> plain_employees_SS1 = new ArrayList<String>();
        List<String> encr_employees_SS1 = new ArrayList<String>();
        List<String> plain_departments_SS1 = new ArrayList<String>();
        List<String> encr_departments_SS1 = new ArrayList<String>();
        List<String> plain_dept_emp_SS1 = new ArrayList<String>();
        List<String> encr_dept_emp_SS1 = new ArrayList<String>();
        List<String> plain_dept_manager_SS1 = new ArrayList<String>();
        List<String> encr_dept_manager_SS1 = new ArrayList<String>();
        List<String> plain_salaries_SS1 = new ArrayList<String>();
        List<String> encr_salaries_SS1 = new ArrayList<String>();
        List<String> plain_titles_SS1 = new ArrayList<String>();
        List<String> encr_titles_SS1 = new ArrayList<String>();

        // Population of the storage tables
        plain_employees_SS1.add("emp_no");
        plain_employees_SS1.add("birth_date");
        plain_employees_SS1.add("first_name");
        plain_employees_SS1.add("last_name");
        plain_employees_SS1.add("gender");
        plain_employees_SS1.add("hire_date");

        plain_departments_SS1.add("dept_no");
        plain_departments_SS1.add("dept_name");

        plain_dept_emp_SS1.add("emp_no");
        plain_dept_emp_SS1.add("dept_no");
        plain_dept_emp_SS1.add("from_date");
        plain_dept_emp_SS1.add("to_date");

        plain_dept_manager_SS1.add("emp_no");
        plain_dept_manager_SS1.add("dept_no");
        plain_dept_manager_SS1.add("from_date");
        plain_dept_manager_SS1.add("to_date");

        plain_salaries_SS1.add("emp_no");
        plain_salaries_SS1.add("salary");
        plain_salaries_SS1.add("from_date");
        plain_salaries_SS1.add("to_date");

        plain_titles_SS1.add("emp_no");
        plain_titles_SS1.add("title");
        plain_titles_SS1.add("from_date");
        plain_titles_SS1.add("to_date");

        // Lists of attributes for the computational node
        List<String> plain_employees_CS1 = new ArrayList<String>();
        List<String> encr_employees_CS1 = new ArrayList<String>();
        List<String> plain_departments_CS1 = new ArrayList<String>();
        List<String> encr_departments_CS1 = new ArrayList<String>();
        List<String> plain_dept_emp_CS1 = new ArrayList<String>();
        List<String> encr_dept_emp_CS1 = new ArrayList<String>();
        List<String> plain_dept_manager_CS1 = new ArrayList<String>();
        List<String> encr_dept_manager_CS1 = new ArrayList<String>();
        List<String> plain_salaries_CS1 = new ArrayList<String>();
        List<String> encr_salaries_CS1 = new ArrayList<String>();
        List<String> plain_titles_CS1 = new ArrayList<String>();
        List<String> encr_titles_CS1 = new ArrayList<String>();

        // Population of the computational tables
        encr_employees_CS1.add("emp_no");
        encr_employees_CS1.add("birth_date");
        encr_employees_CS1.add("first_name");
        encr_employees_CS1.add("last_name");
        encr_employees_CS1.add("gender");
        encr_employees_CS1.add("hire_date");

        encr_departments_CS1.add("dept_no");
        encr_departments_CS1.add("dept_name");

        encr_dept_emp_CS1.add("emp_no");
        encr_dept_emp_CS1.add("dept_no");
        encr_dept_emp_CS1.add("from_date");
        encr_dept_emp_CS1.add("to_date");

        encr_dept_manager_CS1.add("emp_no");
        encr_dept_manager_CS1.add("dept_no");
        encr_dept_manager_CS1.add("from_date");
        encr_dept_manager_CS1.add("to_date");

        encr_salaries_CS1.add("emp_no");
        encr_salaries_CS1.add("salary");
        encr_salaries_CS1.add("from_date");
        encr_salaries_CS1.add("to_date");

        encr_titles_CS1.add("emp_no");
        encr_titles_CS1.add("title");
        encr_titles_CS1.add("from_date");
        encr_titles_CS1.add("to_date");

        // Tables of the client node
        Table tcl1_employees = new Table("employees", plain_employees_CL1, encr_employees_CL1);
        Table tcl1_departments = new Table("departments", plain_departments_CL1, encr_departments_CL1);
        Table tcl1_dept_emp = new Table("dept_emp", plain_dept_emp_CL1, encr_dept_emp_CL1);
        Table tcl1_dept_manager = new Table("dept_manager", plain_dept_manager_CL1, encr_dept_manager_CL1);
        Table tcl1_salaries = new Table("salaries", plain_salaries_CL1, encr_salaries_CL1);
        Table tcl1_titles = new Table("titles", plain_titles_CL1, encr_titles_CL1);

        List<Table> cl1_tables = new ArrayList<>();
        cl1_tables.add(tcl1_employees);
        cl1_tables.add(tcl1_departments);
        cl1_tables.add(tcl1_dept_emp);
        cl1_tables.add(tcl1_dept_manager);
        cl1_tables.add(tcl1_salaries);
        cl1_tables.add(tcl1_titles);

        // Tables of the storage node
        Table tss1_employees = new Table("employees", plain_employees_SS1, encr_employees_SS1);
        Table tss1_departments = new Table("departments", plain_departments_SS1, encr_departments_SS1);
        Table tss1_dept_emp = new Table("dept_emp", plain_dept_emp_SS1, encr_dept_emp_SS1);
        Table tss1_dept_manager = new Table("dept_manager", plain_dept_manager_SS1, encr_dept_manager_SS1);
        Table tss1_salaries = new Table("salaries", plain_salaries_SS1, encr_salaries_SS1);
        Table tss1_titles = new Table("titles", plain_titles_SS1, encr_titles_SS1);

        List<Table> ss1_tables = new ArrayList<>();
        ss1_tables.add(tss1_employees);
        ss1_tables.add(tss1_departments);
        ss1_tables.add(tss1_dept_emp);
        ss1_tables.add(tss1_dept_manager);
        ss1_tables.add(tss1_salaries);
        ss1_tables.add(tss1_titles);

        // Tables of the computational node
        Table tcs1_employees = new Table("employees", plain_employees_CS1, encr_employees_CS1);
        Table tcs1_departments = new Table("departments", plain_departments_CS1, encr_departments_CS1);
        Table tcs1_dept_emp = new Table("dept_emp", plain_dept_emp_CS1, encr_dept_emp_CS1);
        Table tcs1_dept_manager = new Table("dept_manager", plain_dept_manager_CS1, encr_dept_manager_CS1);
        Table tcs1_salaries = new Table("salaries", plain_salaries_CS1, encr_salaries_CS1);
        Table tcs1_titles = new Table("titles", plain_titles_CS1, encr_titles_CS1);

        List<Table> cs1_tables = new ArrayList<>();
        cs1_tables.add(tcs1_employees);
        cs1_tables.add(tcs1_departments);
        cs1_tables.add(tcs1_dept_emp);
        cs1_tables.add(tcs1_dept_manager);
        cs1_tables.add(tcs1_salaries);
        cs1_tables.add(tcs1_titles);

        // Nodes
        Node CL1 = new Node("CL1", "client", ecl1, ccl1, cl1_links, cl1_tables);
        Node SS1 = new Node("SS1", "storage_server", ess1, css1, ss1_links, ss1_tables);
        Node CS1 = new Node("CS1", "computational_server", ecs1, ccs1, cs1_links, cs1_tables);

        nodes = new ArrayList<>();
        nodes.add(CL1);
        nodes.add(SS1);
        nodes.add(CS1);

    }
}
