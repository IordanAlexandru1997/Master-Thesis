package com.example.masterthesisproject.services;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.Record;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

@Service
public class Neo4jService {

    private final String NEO4J_URL = "bolt://localhost:7687";
    private final String USERNAME = "neo4j";
    private final String PASSWORD = "password";
    private Driver driver;

    @PostConstruct
    public void init() {
        driver = GraphDatabase.driver(NEO4J_URL, AuthTokens.basic(USERNAME, PASSWORD));
    }

    public void createRelationships(String employeeName, String projectName, double invoiceAmount) {
        try (Session session = driver.session()) {
            // Ensure Employee, Project, and Invoice nodes exist
            session.run("MERGE (e:Employee {name: $employeeName}) " +
                            "MERGE (p:Project {name: $projectName}) " +
                            "MERGE (i:Invoice {amount: $invoiceAmount})",
                    parameters("employeeName", employeeName,
                            "projectName", projectName,
                            "invoiceAmount", invoiceAmount));

            // Create relationships
            session.run("MATCH (e:Employee {name: $employeeName}), (p:Project {name: $projectName}), (i:Invoice {amount: $invoiceAmount}) " +
                            "MERGE (e)-[:WORKS_FOR]->(p) " +
                            "MERGE (e)-[:ISSUED]->(i)",
                    parameters("employeeName", employeeName,
                            "projectName", projectName,
                            "invoiceAmount", invoiceAmount));
        }
    }
    public void createRelationshipWithoutInvoice(String employeeName, String projectName) {
        try (Session session = driver.session()) {

            session.run("MERGE (e:Employee {name: $employeeName}) " +
                            "MERGE (p:Project {name: $projectName}) ",
                    parameters("employeeName", employeeName,
                            "projectName", projectName));

            // Create relationship
            session.run("MATCH (e:Employee {name: $employeeName}), (p:Project {name: $projectName})" +
                            "MERGE (e)-[:WORKS_FOR]->(p) ",
                    parameters("employeeName", employeeName,
                            "projectName", projectName));
        }
    }

    public List<Map<String, Object>> getEmployeeByName(String name) {
        try (Session session = driver.session()) {
            return session.run("MATCH (e:Employee {name: $name}) RETURN e", Map.of("name", name))
                    .list(r -> r.get("e").asMap());
        }
    }

    public void createEmployee(String name, double salary, String id, String department) {
        try (Session session = driver.session()) {
            String query = "CREATE (e:Employee {name: $name, salary: $salary, id: $id, department: $department})";
            session.run(query, parameters("name", name, "salary", salary, "id", id, "department", department));
        }
    }

    public void createInvoice(String id, String customer, double amount) {
        try (Session session = driver.session()) {
            String query = "CREATE (i:Invoice {id: $id, customer: $customer, amount: $amount})";
            session.run(query, parameters("id", id, "customer", customer, "amount", amount));
        }
    }

    public void createProject(String id, String name) {
        try (Session session = driver.session()) {
            session.run("CREATE (p:Project {id: $id, name: $name})",
                    Map.of("id", id, "name", name));
        }
    }

    public List<Map<String, Object>> getEmployee(String name) {
        try (Session session = driver.session()) {
            String query = "MATCH (e:Employee {name: $name}) RETURN e";
            return session.readTransaction(tx -> tx.run(query, parameters("name", name)).list(r -> r.get("e").asNode().asMap()));
        }
    }


    public List<Map<String, Object>> getInvoice(String id) {
        try (Session session = driver.session()) {
            String query = "MATCH (i:Invoice {id: $id}) RETURN i";
            return session.readTransaction(tx -> tx.run(query, parameters("id", id)).list(r -> r.get("e").asNode().asMap()));
        }
    }

    public List<Map<String, Object>> getProject(String id) {
        try (Session session = driver.session()) {
            return session.run("MATCH (p:Project {id: $id}) RETURN p", Map.of("id", id))
                    .list(r -> r.get("p").asMap());
        }
    }

    public List<Node> getEmployeesByName(String name) {
        List<Node> employees = new ArrayList<>();

        try (Session session = driver.session()) {

            String query = "MATCH (n:Employee) WHERE n.name = $name RETURN n";
            Result result = session.run(query, parameters("name", name));

            while (result.hasNext()) {
                Record record = result.next();
                employees.add(record.get("n").asNode());
            }
        }

        return employees;
    }
}