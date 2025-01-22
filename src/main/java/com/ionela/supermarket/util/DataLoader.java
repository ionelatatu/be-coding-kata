package com.ionela.supermarket.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ionela.supermarket.domain.Item;
import com.ionela.supermarket.domain.Offer;
import com.ionela.supermarket.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;

/**
 * Configuration class used for loading initial data.
 * Reads from a JSON file and populates the H2 database with items and offers.
 */
@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private static final String DATA_FILENAME = "/data.json";

    /**
     * Loads data from a JSON file and saves it to the H2 database at application startup.
     *
     * @param itemRepository the repository used to save item objects
     * @return a CommandLineRunner that executes the data loading logic
     */
    @Bean
    CommandLineRunner loadData(ItemRepository itemRepository) {
        return args -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                InputStream inputStream = getClass().getResourceAsStream(DATA_FILENAME);
                if (inputStream == null) {
                    logger.error("Data file not found: {}", DATA_FILENAME);
                    return;
                }

                List<Item> items = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Item.class));

                for (Item item : items) {
                    if (item.getOffers() != null) {
                        for (Offer offer : item.getOffers()) {
                            offer.setItem(item);
                        }
                    }
                    itemRepository.save(item);
                    logger.info("Saved item: {}", item.getName());
                }

                logger.info("Data successfully loaded into the H2 database");

            } catch (Exception exception) {
                logger.error("Error occurred while loading data: ", exception);
            }
        };
    }
}

