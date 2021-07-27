package musicDirectory.functions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import musicDirectory.modal.Artist;

import java.util.List;
import java.util.stream.Collectors;

import static musicDirectory.constants.Constants.ARTISTS_TABLE;

public class GetAllArtistsFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();
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
}
