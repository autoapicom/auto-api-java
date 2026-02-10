// Auto API Java Client — Complete usage example.
//
// Replace "your-api-key" with your actual API key from https://auto-api.com
//
// Run: cd examples && gradle run

import com.autoapi.client.AutoApiClient;
import com.autoapi.client.exception.ApiException;
import com.autoapi.client.exception.AuthException;
import com.autoapi.client.model.*;
import com.google.gson.Gson;

import java.util.Map;

public class Example {

    public static void main(String[] args) {
        AutoApiClient client = new AutoApiClient("your-api-key");
        Gson gson = new Gson();
        String source = "encar";

        // --- Get available filters ---

        Map<String, Object> filters = client.getFilters(source);
        System.out.printf("Filter keys: %d%n", filters.size());

        // --- Search offers with filters ---

        OffersResponse offers = client.getOffers(source, new OffersParams()
                .page(1)
                .brand("Hyundai")
                .yearFrom(2020)
                .priceTo(50000));

        System.out.printf("%n--- Offers (page %d) ---%n", offers.getMeta().getPage());
        for (OfferItem item : offers.getResult()) {
            OfferData d = gson.fromJson(item.getData(), OfferData.class);
            System.out.printf("%s %s %s — $%s (%s km)%n",
                    d.getMark(), d.getModel(), d.getYear(), d.getPrice(), d.getKmAge());
        }

        // Pagination
        if (offers.getMeta().getNextPage() > 0) {
            OffersResponse nextPage = client.getOffers(source, new OffersParams()
                    .page(offers.getMeta().getNextPage())
                    .brand("Hyundai")
                    .yearFrom(2020));
            System.out.printf("Next page has %d offers%n", nextPage.getResult().size());
        }

        // --- Get single offer ---

        String innerId = "40427050";
        if (!offers.getResult().isEmpty()) {
            innerId = offers.getResult().get(0).getInnerId();
        }

        OffersResponse offer = client.getOffer(source, innerId);
        System.out.printf("%n--- Single offer ---%n");
        if (!offer.getResult().isEmpty()) {
            OfferData d = gson.fromJson(offer.getResult().get(0).getData(), OfferData.class);
            System.out.printf("URL: %s%nSeller: %s%nImages: %d%n",
                    d.getUrl(), d.getSellerType(), d.getImages().size());
        }

        // --- Track changes ---

        int changeId = client.getChangeId(source, "2025-01-15");
        System.out.printf("%n--- Changes from 2025-01-15 (change_id: %d) ---%n", changeId);

        ChangesResponse changes = client.getChanges(source, changeId);
        for (ChangeItem change : changes.getResult()) {
            System.out.printf("[%s] %s%n", change.getChangeType(), change.getInnerId());
        }

        if (changes.getMeta().getNextChangeId() > 0) {
            ChangesResponse moreChanges = client.getChanges(source, changes.getMeta().getNextChangeId());
            System.out.printf("Next batch: %d changes%n", moreChanges.getResult().size());
        }

        // --- Get offer by URL ---

        Map<String, Object> info = client.getOfferByUrl(
                "https://www.encar.com/dc/dc_cardetailview.do?carid=40427050");
        System.out.printf("%n--- Offer by URL ---%n");
        System.out.printf("%s %s — $%s%n", info.get("mark"), info.get("model"), info.get("price"));

        // --- Error handling ---

        AutoApiClient badClient = new AutoApiClient("invalid-key");
        try {
            badClient.getOffers("encar", new OffersParams().page(1));
        } catch (AuthException e) {
            System.out.printf("%nAuth error: %s (HTTP %d)%n", e.getMessage(), e.getStatusCode());
        } catch (ApiException e) {
            System.out.printf("%nAPI error: %s (HTTP %d)%n", e.getMessage(), e.getStatusCode());
            System.out.printf("Body: %s%n", e.getResponseBody());
        }
    }
}
