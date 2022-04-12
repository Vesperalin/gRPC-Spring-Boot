package pl.edu.pwr.rsi.grpc.client;

public class MyStudent {
    private String name;
    private String surname;
    private int indexNumber;
    private String fieldOfStudy;

    public MyStudent(String name, String surname, int indexNumber, String fieldOfStudy) {
        this.name = name;
        this.surname = surname;
        this.indexNumber = indexNumber;
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    @Override
    public String toString(){
        return this.name + " " + this.surname + ", index number: " + this.indexNumber + ", field of study: " + this.fieldOfStudy;
    }
}
