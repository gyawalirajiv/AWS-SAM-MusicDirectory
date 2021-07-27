package musicDirectory.functions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static musicDirectory.constants.Constants.ARTISTS_TABLE;

public class CreateArtistFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());

    public APIGatewayProxyResponseEvent create(final APIGatewayProxyRequestEvent input, final Context context) throws JsonProcessingException {
        musicDirectory.modal.Artist artist = objectMapper.readValue(input.getBody(), musicDirectory.modal.Artist.class);

        Table table = dynamoDB.getTable(System.getenv(ARTISTS_TABLE));
        artist.id = UUID.randomUUID().toString();
        Item item = new Item().withPrimaryKey("id", artist.id)
                .withString("name", artist.name)
                .withString("url", artist.url);
        table.putItem(item);

        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("Artist ID:" + artist.id);
    }
}
