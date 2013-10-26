package tw.edu.ym.guid.querier;

import javafx.beans.property.SimpleStringProperty;
import exceldb.model.Pii;

public class GUID {

  private final SimpleStringProperty date;
  private final SimpleStringProperty guid;
  private final SimpleStringProperty mrn;
  private final SimpleStringProperty nationalId;
  private final SimpleStringProperty lastName;
  private final SimpleStringProperty firstName;
  private final SimpleStringProperty mob;
  private final SimpleStringProperty dob;
  private final SimpleStringProperty yob;
  private final SimpleStringProperty sex;
  private final SimpleStringProperty tel;
  private final SimpleStringProperty address;
  private final SimpleStringProperty doctor;
  private final SimpleStringProperty hospital;

  public GUID(Pii pii) {
    date = new SimpleStringProperty(pii.get編碼日期());
    guid = new SimpleStringProperty(pii.getGuid());
    mrn = new SimpleStringProperty(pii.getMrn());
    nationalId = new SimpleStringProperty(pii.get身份證字號());
    lastName = new SimpleStringProperty(pii.get姓氏());
    firstName = new SimpleStringProperty(pii.get名字());
    mob = new SimpleStringProperty(pii.get出生月());
    dob = new SimpleStringProperty(pii.get出生日());
    yob = new SimpleStringProperty(pii.get出生年());
    sex = new SimpleStringProperty(pii.get性別());
    tel = new SimpleStringProperty(pii.get聯絡電話());
    address = new SimpleStringProperty(pii.get地址());
    doctor = new SimpleStringProperty(pii.get收案醫師());
    hospital = new SimpleStringProperty(pii.get收案醫院名稱());
  }

  public SimpleStringProperty getDate() {
    return date;
  }

  public void setDate(String value) {
    date.set(value);
  }

  public SimpleStringProperty getGuid() {
    return guid;
  }

  public void setGuid(String value) {
    guid.set(value);
  }

  public SimpleStringProperty getMrn() {
    return mrn;
  }

  public void setMrn(String value) {
    mrn.set(value);
  }

  public SimpleStringProperty getNationalId() {
    return nationalId;
  }

  public void setNationalId(String value) {
    nationalId.set(value);
  }

  public SimpleStringProperty getLastName() {
    return lastName;
  }

  public void setLastName(String value) {
    lastName.set(value);
  }

  public SimpleStringProperty getFirstName() {
    return firstName;
  }

  public void setFirstName(String value) {
    firstName.set(value);
  }

  public SimpleStringProperty getMob() {
    return mob;
  }

  public void setMob(String value) {
    mob.set(value);
  }

  public SimpleStringProperty getDob() {
    return dob;
  }

  public void setDob(String value) {
    dob.set(value);
  }

  public SimpleStringProperty getYob() {
    return yob;
  }

  public void setYob(String value) {
    yob.set(value);
  }

  public SimpleStringProperty getSex() {
    return sex;
  }

  public void setSex(String value) {
    sex.set(value);
  }

  public SimpleStringProperty getTel() {
    return tel;
  }

  public void setTel(String value) {
    tel.set(value);
  }

  public SimpleStringProperty getAddress() {
    return address;
  }

  public void setAddress(String value) {
    address.set(value);
  }

  public SimpleStringProperty getDoctor() {
    return doctor;
  }

  public void setDoctor(String value) {
    doctor.set(value);
  }

  public SimpleStringProperty getHospital() {
    return hospital;
  }

  public void getHospital(String value) {
    hospital.set(value);
  }

}
