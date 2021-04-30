package br.bd2.constants;

public enum Keywords {
    select("select"),
    from("from"),
    where("where"),
    join("join"),
    order_by("order by");
//    equal("="),
//    greater(">"),
//    less("<"),
//    greater_or_equal(">="),
//    less_or_equal("<="),
//    not_equal("<>"),
//    and("and"),
//    or("or"),
//    in("in"),
//    not_in("not in"),
//    not("not"),
//    like("like"),
//    asc("asc"),
//    desc("desc"),
//    on("on");

    private final String keyword;

    Keywords(String keyword) {
        this.keyword = keyword;
    }

    public static boolean hasEqual(String word) {
        for (Keywords keyword: Keywords.values()) {
            if (keyword.toString().equals(word.toLowerCase())) return true;
        }
        return false;
    }

    public String toString() {
        return this.keyword;
    }
}
