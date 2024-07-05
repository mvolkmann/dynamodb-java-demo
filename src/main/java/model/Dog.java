package model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Dog {

    private int id;
    private String name;
    private String breed;

    @DynamoDbPartitionKey
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getBreed() { return this.breed; }
    public void setBreed(String breed) { this.breed = breed; }

    @Override
    public String toString() {
        return "Dog [id=" + id + ", name=" + name + ", breed=" + breed  + "]";
    }
}