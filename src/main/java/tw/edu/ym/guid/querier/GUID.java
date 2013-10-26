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

  public String getDate() {
    return date.getValue();
  }

  public void setDate(String value) {
    date.set(value);
  }

  public String getGuid() {
    return guid.getValue();
  }

  public void setGuid(String value) {
    guid.set(value);
  }

  public String getMrn() {
    return mrn.getValue();
  }

  public void setMrn(String value) {
    mrn.set(value);
  }

  public String getNationalId() {
    return nationalId.getValue();
  }

  public void setNationalId(String value) {
    nationalId.set(value);
  }

  public String getLastName() {
    return lastName.getValue();
  }

  public void setLastName(String value) {
    lastName.set(value);
  }

  public String getFirstName() {
    return firstName.getValue();
  }

  public void setFirstName(String value) {
    firstName.set(value);
  }

  public String getMob() {
    return mob.getValue();
  }

  public void setMob(String value) {
    mob.set(value);
  }

  public String getDob() {
    return dob.getValue();
  }

  public void setDob(String value) {
    dob.set(value);
  }

  public String getYob() {
    return yob.getValue();
  }

  public void setYob(String value) {
    yob.set(value);
  }

  public String getSex() {
    return sex.getValue();
  }

  public void setSex(String value) {
    sex.set(value);
  }

  public String getTel() {
    return tel.getValue();
  }

  public void setTel(String value) {
    tel.set(value);
  }

  public String getAddress() {
    return address.getValue();
  }

  public void setAddress(String value) {
    address.set(value);
  }

  public String getDoctor() {
    return doctor.getValue();
  }

  public void setDoctor(String value) {
    doctor.set(value);
  }

  public String getHospital() {
    return hospital.getValue();
  }

  public void getHospital(String value) {
    hospital.set(value);
  }

}
