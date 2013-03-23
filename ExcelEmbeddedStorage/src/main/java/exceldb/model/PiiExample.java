package exceldb.model;

import java.util.ArrayList;
import java.util.List;

public class PiiExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table PII
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table PII
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table PII
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public PiiExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PII
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table PII
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andLocalIdIsNull() {
            addCriterion("LOCAL_ID is null");
            return (Criteria) this;
        }

        public Criteria andLocalIdIsNotNull() {
            addCriterion("LOCAL_ID is not null");
            return (Criteria) this;
        }

        public Criteria andLocalIdEqualTo(String value) {
            addCriterion("LOCAL_ID =", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdNotEqualTo(String value) {
            addCriterion("LOCAL_ID <>", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdGreaterThan(String value) {
            addCriterion("LOCAL_ID >", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdGreaterThanOrEqualTo(String value) {
            addCriterion("LOCAL_ID >=", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdLessThan(String value) {
            addCriterion("LOCAL_ID <", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdLessThanOrEqualTo(String value) {
            addCriterion("LOCAL_ID <=", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdLike(String value) {
            addCriterion("LOCAL_ID like", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdNotLike(String value) {
            addCriterion("LOCAL_ID not like", value, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdIn(List<String> values) {
            addCriterion("LOCAL_ID in", values, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdNotIn(List<String> values) {
            addCriterion("LOCAL_ID not in", values, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdBetween(String value1, String value2) {
            addCriterion("LOCAL_ID between", value1, value2, "localId");
            return (Criteria) this;
        }

        public Criteria andLocalIdNotBetween(String value1, String value2) {
            addCriterion("LOCAL_ID not between", value1, value2, "localId");
            return (Criteria) this;
        }

        public Criteria andGuidIsNull() {
            addCriterion("GUID is null");
            return (Criteria) this;
        }

        public Criteria andGuidIsNotNull() {
            addCriterion("GUID is not null");
            return (Criteria) this;
        }

        public Criteria andGuidEqualTo(String value) {
            addCriterion("GUID =", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidNotEqualTo(String value) {
            addCriterion("GUID <>", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidGreaterThan(String value) {
            addCriterion("GUID >", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidGreaterThanOrEqualTo(String value) {
            addCriterion("GUID >=", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidLessThan(String value) {
            addCriterion("GUID <", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidLessThanOrEqualTo(String value) {
            addCriterion("GUID <=", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidLike(String value) {
            addCriterion("GUID like", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidNotLike(String value) {
            addCriterion("GUID not like", value, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidIn(List<String> values) {
            addCriterion("GUID in", values, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidNotIn(List<String> values) {
            addCriterion("GUID not in", values, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidBetween(String value1, String value2) {
            addCriterion("GUID between", value1, value2, "guid");
            return (Criteria) this;
        }

        public Criteria andGuidNotBetween(String value1, String value2) {
            addCriterion("GUID not between", value1, value2, "guid");
            return (Criteria) this;
        }

        public Criteria andMrnIsNull() {
            addCriterion("MRN is null");
            return (Criteria) this;
        }

        public Criteria andMrnIsNotNull() {
            addCriterion("MRN is not null");
            return (Criteria) this;
        }

        public Criteria andMrnEqualTo(String value) {
            addCriterion("MRN =", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnNotEqualTo(String value) {
            addCriterion("MRN <>", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnGreaterThan(String value) {
            addCriterion("MRN >", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnGreaterThanOrEqualTo(String value) {
            addCriterion("MRN >=", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnLessThan(String value) {
            addCriterion("MRN <", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnLessThanOrEqualTo(String value) {
            addCriterion("MRN <=", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnLike(String value) {
            addCriterion("MRN like", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnNotLike(String value) {
            addCriterion("MRN not like", value, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnIn(List<String> values) {
            addCriterion("MRN in", values, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnNotIn(List<String> values) {
            addCriterion("MRN not in", values, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnBetween(String value1, String value2) {
            addCriterion("MRN between", value1, value2, "mrn");
            return (Criteria) this;
        }

        public Criteria andMrnNotBetween(String value1, String value2) {
            addCriterion("MRN not between", value1, value2, "mrn");
            return (Criteria) this;
        }

        public Criteria and身份證字號IsNull() {
            addCriterion("身份證字號 is null");
            return (Criteria) this;
        }

        public Criteria and身份證字號IsNotNull() {
            addCriterion("身份證字號 is not null");
            return (Criteria) this;
        }

        public Criteria and身份證字號EqualTo(String value) {
            addCriterion("身份證字號 =", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號NotEqualTo(String value) {
            addCriterion("身份證字號 <>", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號GreaterThan(String value) {
            addCriterion("身份證字號 >", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號GreaterThanOrEqualTo(String value) {
            addCriterion("身份證字號 >=", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號LessThan(String value) {
            addCriterion("身份證字號 <", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號LessThanOrEqualTo(String value) {
            addCriterion("身份證字號 <=", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號Like(String value) {
            addCriterion("身份證字號 like", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號NotLike(String value) {
            addCriterion("身份證字號 not like", value, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號In(List<String> values) {
            addCriterion("身份證字號 in", values, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號NotIn(List<String> values) {
            addCriterion("身份證字號 not in", values, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號Between(String value1, String value2) {
            addCriterion("身份證字號 between", value1, value2, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and身份證字號NotBetween(String value1, String value2) {
            addCriterion("身份證字號 not between", value1, value2, "身份證字號");
            return (Criteria) this;
        }

        public Criteria and姓氏IsNull() {
            addCriterion("姓氏 is null");
            return (Criteria) this;
        }

        public Criteria and姓氏IsNotNull() {
            addCriterion("姓氏 is not null");
            return (Criteria) this;
        }

        public Criteria and姓氏EqualTo(String value) {
            addCriterion("姓氏 =", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏NotEqualTo(String value) {
            addCriterion("姓氏 <>", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏GreaterThan(String value) {
            addCriterion("姓氏 >", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏GreaterThanOrEqualTo(String value) {
            addCriterion("姓氏 >=", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏LessThan(String value) {
            addCriterion("姓氏 <", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏LessThanOrEqualTo(String value) {
            addCriterion("姓氏 <=", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏Like(String value) {
            addCriterion("姓氏 like", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏NotLike(String value) {
            addCriterion("姓氏 not like", value, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏In(List<String> values) {
            addCriterion("姓氏 in", values, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏NotIn(List<String> values) {
            addCriterion("姓氏 not in", values, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏Between(String value1, String value2) {
            addCriterion("姓氏 between", value1, value2, "姓氏");
            return (Criteria) this;
        }

        public Criteria and姓氏NotBetween(String value1, String value2) {
            addCriterion("姓氏 not between", value1, value2, "姓氏");
            return (Criteria) this;
        }

        public Criteria and名字IsNull() {
            addCriterion("名字 is null");
            return (Criteria) this;
        }

        public Criteria and名字IsNotNull() {
            addCriterion("名字 is not null");
            return (Criteria) this;
        }

        public Criteria and名字EqualTo(String value) {
            addCriterion("名字 =", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字NotEqualTo(String value) {
            addCriterion("名字 <>", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字GreaterThan(String value) {
            addCriterion("名字 >", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字GreaterThanOrEqualTo(String value) {
            addCriterion("名字 >=", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字LessThan(String value) {
            addCriterion("名字 <", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字LessThanOrEqualTo(String value) {
            addCriterion("名字 <=", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字Like(String value) {
            addCriterion("名字 like", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字NotLike(String value) {
            addCriterion("名字 not like", value, "名字");
            return (Criteria) this;
        }

        public Criteria and名字In(List<String> values) {
            addCriterion("名字 in", values, "名字");
            return (Criteria) this;
        }

        public Criteria and名字NotIn(List<String> values) {
            addCriterion("名字 not in", values, "名字");
            return (Criteria) this;
        }

        public Criteria and名字Between(String value1, String value2) {
            addCriterion("名字 between", value1, value2, "名字");
            return (Criteria) this;
        }

        public Criteria and名字NotBetween(String value1, String value2) {
            addCriterion("名字 not between", value1, value2, "名字");
            return (Criteria) this;
        }

        public Criteria and出生月IsNull() {
            addCriterion("出生月 is null");
            return (Criteria) this;
        }

        public Criteria and出生月IsNotNull() {
            addCriterion("出生月 is not null");
            return (Criteria) this;
        }

        public Criteria and出生月EqualTo(String value) {
            addCriterion("出生月 =", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月NotEqualTo(String value) {
            addCriterion("出生月 <>", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月GreaterThan(String value) {
            addCriterion("出生月 >", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月GreaterThanOrEqualTo(String value) {
            addCriterion("出生月 >=", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月LessThan(String value) {
            addCriterion("出生月 <", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月LessThanOrEqualTo(String value) {
            addCriterion("出生月 <=", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月Like(String value) {
            addCriterion("出生月 like", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月NotLike(String value) {
            addCriterion("出生月 not like", value, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月In(List<String> values) {
            addCriterion("出生月 in", values, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月NotIn(List<String> values) {
            addCriterion("出生月 not in", values, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月Between(String value1, String value2) {
            addCriterion("出生月 between", value1, value2, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生月NotBetween(String value1, String value2) {
            addCriterion("出生月 not between", value1, value2, "出生月");
            return (Criteria) this;
        }

        public Criteria and出生日IsNull() {
            addCriterion("出生日 is null");
            return (Criteria) this;
        }

        public Criteria and出生日IsNotNull() {
            addCriterion("出生日 is not null");
            return (Criteria) this;
        }

        public Criteria and出生日EqualTo(String value) {
            addCriterion("出生日 =", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日NotEqualTo(String value) {
            addCriterion("出生日 <>", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日GreaterThan(String value) {
            addCriterion("出生日 >", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日GreaterThanOrEqualTo(String value) {
            addCriterion("出生日 >=", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日LessThan(String value) {
            addCriterion("出生日 <", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日LessThanOrEqualTo(String value) {
            addCriterion("出生日 <=", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日Like(String value) {
            addCriterion("出生日 like", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日NotLike(String value) {
            addCriterion("出生日 not like", value, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日In(List<String> values) {
            addCriterion("出生日 in", values, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日NotIn(List<String> values) {
            addCriterion("出生日 not in", values, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日Between(String value1, String value2) {
            addCriterion("出生日 between", value1, value2, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生日NotBetween(String value1, String value2) {
            addCriterion("出生日 not between", value1, value2, "出生日");
            return (Criteria) this;
        }

        public Criteria and出生年IsNull() {
            addCriterion("出生年 is null");
            return (Criteria) this;
        }

        public Criteria and出生年IsNotNull() {
            addCriterion("出生年 is not null");
            return (Criteria) this;
        }

        public Criteria and出生年EqualTo(String value) {
            addCriterion("出生年 =", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年NotEqualTo(String value) {
            addCriterion("出生年 <>", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年GreaterThan(String value) {
            addCriterion("出生年 >", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年GreaterThanOrEqualTo(String value) {
            addCriterion("出生年 >=", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年LessThan(String value) {
            addCriterion("出生年 <", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年LessThanOrEqualTo(String value) {
            addCriterion("出生年 <=", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年Like(String value) {
            addCriterion("出生年 like", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年NotLike(String value) {
            addCriterion("出生年 not like", value, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年In(List<String> values) {
            addCriterion("出生年 in", values, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年NotIn(List<String> values) {
            addCriterion("出生年 not in", values, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年Between(String value1, String value2) {
            addCriterion("出生年 between", value1, value2, "出生年");
            return (Criteria) this;
        }

        public Criteria and出生年NotBetween(String value1, String value2) {
            addCriterion("出生年 not between", value1, value2, "出生年");
            return (Criteria) this;
        }

        public Criteria and聯絡電話IsNull() {
            addCriterion("聯絡電話 is null");
            return (Criteria) this;
        }

        public Criteria and聯絡電話IsNotNull() {
            addCriterion("聯絡電話 is not null");
            return (Criteria) this;
        }

        public Criteria and聯絡電話EqualTo(String value) {
            addCriterion("聯絡電話 =", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話NotEqualTo(String value) {
            addCriterion("聯絡電話 <>", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話GreaterThan(String value) {
            addCriterion("聯絡電話 >", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話GreaterThanOrEqualTo(String value) {
            addCriterion("聯絡電話 >=", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話LessThan(String value) {
            addCriterion("聯絡電話 <", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話LessThanOrEqualTo(String value) {
            addCriterion("聯絡電話 <=", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話Like(String value) {
            addCriterion("聯絡電話 like", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話NotLike(String value) {
            addCriterion("聯絡電話 not like", value, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話In(List<String> values) {
            addCriterion("聯絡電話 in", values, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話NotIn(List<String> values) {
            addCriterion("聯絡電話 not in", values, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話Between(String value1, String value2) {
            addCriterion("聯絡電話 between", value1, value2, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and聯絡電話NotBetween(String value1, String value2) {
            addCriterion("聯絡電話 not between", value1, value2, "聯絡電話");
            return (Criteria) this;
        }

        public Criteria and性別IsNull() {
            addCriterion("性別 is null");
            return (Criteria) this;
        }

        public Criteria and性別IsNotNull() {
            addCriterion("性別 is not null");
            return (Criteria) this;
        }

        public Criteria and性別EqualTo(String value) {
            addCriterion("性別 =", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別NotEqualTo(String value) {
            addCriterion("性別 <>", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別GreaterThan(String value) {
            addCriterion("性別 >", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別GreaterThanOrEqualTo(String value) {
            addCriterion("性別 >=", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別LessThan(String value) {
            addCriterion("性別 <", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別LessThanOrEqualTo(String value) {
            addCriterion("性別 <=", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別Like(String value) {
            addCriterion("性別 like", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別NotLike(String value) {
            addCriterion("性別 not like", value, "性別");
            return (Criteria) this;
        }

        public Criteria and性別In(List<String> values) {
            addCriterion("性別 in", values, "性別");
            return (Criteria) this;
        }

        public Criteria and性別NotIn(List<String> values) {
            addCriterion("性別 not in", values, "性別");
            return (Criteria) this;
        }

        public Criteria and性別Between(String value1, String value2) {
            addCriterion("性別 between", value1, value2, "性別");
            return (Criteria) this;
        }

        public Criteria and性別NotBetween(String value1, String value2) {
            addCriterion("性別 not between", value1, value2, "性別");
            return (Criteria) this;
        }

        public Criteria and收案醫師IsNull() {
            addCriterion("收案醫師 is null");
            return (Criteria) this;
        }

        public Criteria and收案醫師IsNotNull() {
            addCriterion("收案醫師 is not null");
            return (Criteria) this;
        }

        public Criteria and收案醫師EqualTo(String value) {
            addCriterion("收案醫師 =", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師NotEqualTo(String value) {
            addCriterion("收案醫師 <>", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師GreaterThan(String value) {
            addCriterion("收案醫師 >", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師GreaterThanOrEqualTo(String value) {
            addCriterion("收案醫師 >=", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師LessThan(String value) {
            addCriterion("收案醫師 <", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師LessThanOrEqualTo(String value) {
            addCriterion("收案醫師 <=", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師Like(String value) {
            addCriterion("收案醫師 like", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師NotLike(String value) {
            addCriterion("收案醫師 not like", value, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師In(List<String> values) {
            addCriterion("收案醫師 in", values, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師NotIn(List<String> values) {
            addCriterion("收案醫師 not in", values, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師Between(String value1, String value2) {
            addCriterion("收案醫師 between", value1, value2, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and收案醫師NotBetween(String value1, String value2) {
            addCriterion("收案醫師 not between", value1, value2, "收案醫師");
            return (Criteria) this;
        }

        public Criteria and醫院名稱IsNull() {
            addCriterion("醫院名稱 is null");
            return (Criteria) this;
        }

        public Criteria and醫院名稱IsNotNull() {
            addCriterion("醫院名稱 is not null");
            return (Criteria) this;
        }

        public Criteria and醫院名稱EqualTo(String value) {
            addCriterion("醫院名稱 =", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱NotEqualTo(String value) {
            addCriterion("醫院名稱 <>", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱GreaterThan(String value) {
            addCriterion("醫院名稱 >", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱GreaterThanOrEqualTo(String value) {
            addCriterion("醫院名稱 >=", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱LessThan(String value) {
            addCriterion("醫院名稱 <", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱LessThanOrEqualTo(String value) {
            addCriterion("醫院名稱 <=", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱Like(String value) {
            addCriterion("醫院名稱 like", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱NotLike(String value) {
            addCriterion("醫院名稱 not like", value, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱In(List<String> values) {
            addCriterion("醫院名稱 in", values, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱NotIn(List<String> values) {
            addCriterion("醫院名稱 not in", values, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱Between(String value1, String value2) {
            addCriterion("醫院名稱 between", value1, value2, "醫院名稱");
            return (Criteria) this;
        }

        public Criteria and醫院名稱NotBetween(String value1, String value2) {
            addCriterion("醫院名稱 not between", value1, value2, "醫院名稱");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table PII
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table PII
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}