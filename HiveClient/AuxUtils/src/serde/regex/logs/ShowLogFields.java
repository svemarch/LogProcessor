package serde.regex.logs;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/24/12
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShowLogFields extends LogFields{
    static {
        fieldPatterns.put("dt", "^[^\t]*(\\d{4}-\\d{2}-\\d{2})");
        fieldFormats.put("dt", "%s\t");

        fieldPatterns.put("request_id", "reqid=([^\t]+)\t");
        fieldFormats.put("request_id", "reqid=%s\t");

        fieldPatterns.put("position_num", "num=([^@]+)@@");
        fieldFormats.put("position_num", "num=%d\t");

        fieldPatterns.put("company_id", "~~id=([^@]+)@@");
        fieldFormats.put("company_id", "~~id=%s\t");

        fieldPatterns.put("base", "base=([^@]+)@@");
        fieldFormats.put("base", "base=%s\t");

        fieldPatterns.put("region_id", "reg=([^\t]+)");
        fieldFormats.put("region_id", "reg=%d\t");

    }
}
