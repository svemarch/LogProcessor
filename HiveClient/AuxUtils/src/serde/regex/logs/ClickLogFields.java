package serde.regex.logs;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/24/12
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClickLogFields extends LogFields{
    static {
        fieldPatterns.put("dt", "^[^\t]*(\\d{4}-\\d{2}-\\d{2})");
        fieldFormats.put("dt", "%s\t");

        fieldPatterns.put("request_id", "reqid=([^\t]+)\t");
        fieldFormats.put("request_id", "reqid=%s\t");

        fieldPatterns.put("position_num", "vars=pos=p([^\t]+)\t");
        fieldFormats.put("position_num", "vars=pos=p%s\t");

        fieldPatterns.put("path", "path=([^\t]+)\t");
        fieldFormats.put("path", "path=%s\t");
    }

}
