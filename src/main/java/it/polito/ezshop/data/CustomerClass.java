package it.polito.ezshop.data;

public class CustomerClass implements Customer{

    private Integer id;
    private String name;
    private String card;
    private Integer points;

    public CustomerClass(Integer id, String name, String card, Integer points) {
        this.id = id;
        this.name = name;
        this.card = card;
        this.points = points;
    }

    public CustomerClass(){
        this.id = -1;
        this.name = "";
        this.card = "";
        this.points = -1;
    }

    @Override
    public String getCustomerName() {
        return name;
    }

    @Override
    public void setCustomerName(String customerName) {
        name=customerName;
    }

    @Override
    public String getCustomerCard() {
        return card;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        card=customerCard;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id=id;
    }

    @Override
    public Integer getPoints() {
        return points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points=points;
    }

    public boolean attachLoyaltyCard(String card_code){
        String regex = "[0-9]*";
        if(card_code.length()!=10 || !card_code.matches(regex))
            return false;
        this.card=card_code;
        this.points=0;
        return true;
    }

    public boolean updatePoints(Integer pts){
        if((pts + points)>=0) {
            points += pts;
            return true;
        }
        else return false;
    }
}
