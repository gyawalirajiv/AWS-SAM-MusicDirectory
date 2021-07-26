package musicDirectory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import modal.Artist;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class ArtistResource {

    public static final String ARTISTS_TABLE = "ARTISTS_TABLE";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());

    public APIGatewayProxyResponseEvent create(final APIGatewayProxyRequestEvent input, final Context context) throws JsonProcessingException {
        modal.Artist artist = objectMapper.readValue(input.getBody(), modal.Artist.class);

        Table table = dynamoDB.getTable(System.getenv(ARTISTS_TABLE));
        artist.id = UUID.randomUUID().toString();
        Item item = new Item().withPrimaryKey("id", artist.id)
                .withString("name", artist.name)
                .withString("url", artist.url);
        table.putItem(item);

        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("Artist ID:" + artist.id);
    }

    public APIGatewayProxyResponseEvent getAll(final APIGatewayProxyRequestEvent input, final Context context) throws JsonProcessingException {
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        ScanResult scanResult = dynamoDB.scan(new ScanRequest().withTableName(System.getenv(ARTISTS_TABLE)));
        List<Artist> artists = scanResult.getItems().stream().map(item -> new Artist(
                item.get("id").getS(),
                item.get("name").getS(),
                item.get("url").getS()
                )).collect(Collectors.toList());
        String jsonOutput = objectMapper.writeValueAsString(artists);
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonOutput);
    }

    public APIGatewayProxyResponseEvent get(final APIGatewayProxyRequestEvent input, final Context context) throws JsonProcessingException {
        Item item = dynamoDB.getTable(System.getenv(ARTISTS_TABLE)).getItem("id", input.getPathParameters().get("id"));
        Artist artist = new Artist(item.get("id").toString(),
                item.get("name").toString(),
                item.get("url").toString());
        String jsonOutput = objectMapper.writeValueAsString(artist);
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonOutput);
    }

    public APIGatewayProxyResponseEvent update(final APIGatewayProxyRequestEvent input, final Context context) throws JsonProcessingException {
        Artist payload = objectMapper.readValue(input.getBody(), Artist.class);

        Table table = dynamoDB.getTable(System.getenv(ARTISTS_TABLE));
        Item item = new Item().withPrimaryKey("id", input.getPathParameters().get("id"))
                .withString("name", payload.name)
                .withString("url", payload.url);
        table.putItem(item);

        String jsonOutput = objectMapper.writeValueAsString(payload);
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonOutput);
    }

    public APIGatewayProxyResponseEvent delete(final APIGatewayProxyRequestEvent input, final Context context) {
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();

        Map<String, AttributeValue> map = new HashMap<>();
        map.put("id", new AttributeValue(input.getPathParameters().get("id")));
        dynamoDB.deleteItem(System.getenv(ARTISTS_TABLE ), map);

        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }

}
