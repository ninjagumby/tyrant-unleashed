package tyrant;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TyrantProcessor {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TyrantProcessor.class);

    private List<String> cardResources_ = new ArrayList<>();

    public TyrantProcessor() {
        cardResources_.add("cards_section_1.xml");
        cardResources_.add("cards_section_2.xml");
        cardResources_.add("cards_section_3.xml");
        cardResources_.add("cards_section_4.xml");
        cardResources_.add("cards_section_5.xml");
        cardResources_.add("cards_section_6.xml");
        cardResources_.add("cards_section_7.xml");
        cardResources_.add("cards_section_8.xml");
        cardResources_.add("cards_section_9.xml");
        cardResources_.add("cards_section_10.xml");
        cardResources_.add("cards_section_11.xml");
    }

    public Map<String, String> loadCardsFromInternet() {
        Map<String, String> cards = new TreeMap<>();
        for (String cardResource : cardResources_) {
            try {
                URL url = new URL("https://mobile.tyrantonline.com/assets/"
                        + cardResource);
                LOGGER.info("Loading card resources from " + url.toString());
                InputStream in = url.openStream();
                cards.putAll(loadCardSection(in));

                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return cards;
    }

    public Map<String, String> loadCardFromLocalResources() {
        Map<String, String> cards = new TreeMap<>();

        for (String cardResource : cardResources_) {
            InputStream in = this.getClass().getClassLoader()
                    .getResourceAsStream("cards/" + cardResource);
            cards.putAll(loadCardSection(in));
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return cards;
    }

    public Map<String, String> loadCardSection(InputStream in) {
        Map<String, String> cards = new TreeMap<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(in);

            // optional, but recommended
            // read this -
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList unitList = doc.getElementsByTagName("unit");

            for (int unit = 0; unit < unitList.getLength(); unit++) {
                Node unitNode = unitList.item(unit);

                if (unitNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element unitElement = (Element) unitNode;

                    String unitName = unitElement.getElementsByTagName("name")
                            .item(0).getTextContent();
                    LOGGER.info("Card "
                            + unitElement.getElementsByTagName("id").item(0)
                                    .getTextContent()
                            + " : " + unitName + "-1");
                    cards.put(unitElement.getElementsByTagName("id").item(0)
                            .getTextContent(), unitName + "-1");

                    NodeList upgradeList = unitElement
                            .getElementsByTagName("upgrade");
                    for (int upgrade = 0; upgrade < upgradeList
                            .getLength(); upgrade++) {
                        Node upgradeNode = upgradeList.item(upgrade);

                        if (unitNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element upgradeElement = (Element) upgradeNode;
                            LOGGER.info("Card "
                                    + upgradeElement
                                            .getElementsByTagName("card_id")
                                            .item(0).getTextContent()
                                    + " : " + unitName + "-"
                                    + upgradeElement
                                            .getElementsByTagName("level")
                                            .item(0).getTextContent());
                            cards.put(
                                    upgradeElement
                                            .getElementsByTagName("card_id")
                                            .item(0).getTextContent(),
                                    unitName + "-" + upgradeElement
                                            .getElementsByTagName("level")
                                            .item(0).getTextContent());
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cards;
    }

    public List<UserCard> processUserAccount(String jsonString,
            Map<String, String> cards) {
        List<UserCard> userCardDetails = new ArrayList<>();

        JSONObject obj = new JSONObject(jsonString);
        JSONObject userCardsJson = obj.getJSONObject("user_cards");
        LOGGER.info(userCardsJson.length() + " user cards.");

        Iterator<?> userCards = userCardsJson.keys();

        while (userCards.hasNext()) {
            String userCardKey = (String) userCards.next();
            if (userCardsJson.get(userCardKey) instanceof JSONObject) {
                JSONObject userCard = (JSONObject) userCardsJson
                        .get(userCardKey);
                LOGGER.info(cards.get(userCardKey) + " (" + userCardKey
                        + ") : Num Owned " + userCard.getString("num_owned"));
                if (userCard.getInt("num_owned") > 0) {
                    userCardDetails.add(
                            new UserCard(userCardKey, cards.get(userCardKey),
                                    userCard.getInt("num_owned"),
                                    userCard.getInt("num_owned")));
                }
            }
        }
        return userCardDetails;
    }

}
