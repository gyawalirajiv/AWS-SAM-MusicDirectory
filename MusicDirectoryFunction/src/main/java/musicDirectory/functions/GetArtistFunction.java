package musicDirectory.functions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import musicDirectory.modal.Artist;

import static musicDirectory.constants.Constants.ARTISTS_TABLE;

public class GetArtistFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());

    public APIGatewayProxyResponseEvent get(final APIGatewayProxyRequestEvent input, final Context context) throws JsonProcessingException {
        Item item = dynamoDB.getTable(System.getenv(ARTISTS_TABLE)).getItem("id", input.getPathParameters().get("id"));
        Artist artist = new Artist(item.get("id").toString(),
                item.get("name").toString(),
                item.get("url").toString());
        String jsonOutput = objectMapper.writeValueAsString(artist);
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonOutput);
    }
}
