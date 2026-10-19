package Model.Transactions;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Transaction {
    private final SimpleIntegerProperty number;
    private final SimpleStringProperty name;
    private final SimpleStringProperty description;
    private final SimpleStringProperty date;
    private final SimpleStringProperty location;

    public Transaction(int number, String name, String description, String date, String location) {
        this.number = new SimpleIntegerProperty(number);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.date = new SimpleStringProperty(date);
        this.location = new SimpleStringProperty(location);
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public int getNumber() {
        return this.number.get();
    }

    public SimpleIntegerProperty numberProperty() {
        return this.number;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return this.name.get();
    }

    public SimpleStringProperty nameProperty() {
        return this.name;
    }

    public void setDescription(String name) {
        this.description.set(name);
    }

    public String getDescription() {
        return this.description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return this.description;
    }

    public void setDate(String name) {
        this.date.set(name);
    }

    public String getDate() {
        return this.date.get();
    }

    public SimpleStringProperty dateProperty() {
        return this.date;
    }

    public void setLocation(String name) {
        this.location.set(name);
    }

    public String getLocation() {
        return this.location.get();
    }

    public SimpleStringProperty locationProperty() {
        return this.location;
    }
}
