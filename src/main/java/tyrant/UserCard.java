package tyrant;

public class UserCard {
    private String cardId_;
    private String cardName_;
    private Integer numOwned_;
    private Integer numUsed_;

    public UserCard(String id, String name, Integer owned, Integer used) {
        cardId_ = id;
        cardName_ = name;
        numOwned_ = owned;
        numUsed_ = used;
    }

    public String getCardId() {
        return cardId_;
    }

    public String getCardName() {
        return cardName_;
    }

    public Integer getNumOwned() {
        return numOwned_;
    }

    public Integer getNumUsed() {
        return numUsed_;
    }

    public String toString() {
        return cardName_ + "[" + numOwned_ + " owned, " + numUsed_ + " used]";
    }

}
