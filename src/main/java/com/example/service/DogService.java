package com.example.service;

import com.example.model.Dog;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.*;

public class DogService {

    private final DynamoDbTable<Dog> dogsTable;

    public DogService() {
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .endpointDiscoveryEnabled(true)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
        dogsTable =
                enhancedClient.table("Dogs", TableSchema.fromBean(Dog.class));
    }

    public String add(String name, String breed) {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        Dog dog = new Dog(id, name, breed);
        try {
            dogsTable.putItem(dog);
            return id;
        } catch (DynamoDbException e) {
            System.err.println("add: " + e.getMessage());
            System.exit(1);
            return "";
        }
    }

    public void delete(String id) {
        try {
            Key key = Key.builder().partitionValue(id).build();
            dogsTable.deleteItem(key);
        } catch (DynamoDbException e) {
            System.err.println("delete: " + e.getMessage());
            System.exit(1);
        }
    }

    public void deleteAll() {
        try {
            Iterator<Dog> dogs = this.getAllIterator();
            while (dogs.hasNext()) {
                Dog dog = dogs.next();
                this.delete(dog.getId());
            }
        } catch (DynamoDbException e) {
            System.err.println("deleteAll: " + e.getMessage());
            System.exit(1);
        }
    }

    public Dog get(String id) {
        Key key = Key.builder().partitionValue(id).build();
        try {
            return dogsTable.getItem(key);
        } catch (DynamoDbException e) {
            System.err.println("get: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public Iterator<Dog> getAllIterator() {
        return dogsTable.scan().items().iterator();
    }

    /**
     * Returns a list of Dog objects sorted in ascending order on their names.
     */
    public List<Dog> getAllList() {
        SortedSet<Dog> set = new TreeSet<>();
        Iterator<Dog> iter = this.getAllIterator();
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return new ArrayList<>(set);
    }

    public void printAll() {
        Iterator<Dog> dogs = this.getAllIterator();
        while (dogs.hasNext()) {
            Dog dog = dogs.next();
            System.out.println(">>> " + dog);
        }
    }

    public void rename(String id, String newName) {
        Dog dog = this.get(id);
        dog.setName(newName);
        try {
            dogsTable.putItem(dog);
        } catch (DynamoDbException e) {
            System.err.println("rename: " + e.getMessage());
            System.exit(1);
        }
    }
}