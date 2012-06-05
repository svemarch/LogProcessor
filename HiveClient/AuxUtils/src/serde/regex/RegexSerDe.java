package serde.regex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.Constants;
import org.apache.hadoop.hive.serde2.SerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.*;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import serde.regex.logs.ClickLogFields;
import serde.regex.logs.LogFields;
import serde.regex.logs.ShowLogFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/24/12
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegexSerDe implements SerDe {
    int numColumns;
    LogFields inputLogFields;
    
    StructObjectInspector rowOI;
    List<Object> row;
    TypeInfo rowTypeInfo;
    List<TypeInfo> columnTypes;
    List<String> columnNames;
    
    Object[] outputFields;
    Text outputRowText;

    @Override
    public void initialize(Configuration entries, Properties properties) throws SerDeException {
        String inputLogName = properties.getProperty("input.log");
        String columnNameProperty = properties.getProperty(Constants.LIST_COLUMNS);
        String columnTypeProperty = properties.getProperty(Constants.LIST_COLUMN_TYPES);

        if (inputLogName == null)
            throw new SerDeException("This table does not have serde property \"input.log\"!");

        if (inputLogName.equals("showLog")) {
            this.inputLogFields = new ShowLogFields();
        } else if (inputLogName.equals("clickLog")) {
            this.inputLogFields = new ClickLogFields();
        } else {
            this.inputLogFields = null;
        }
        columnNames = Arrays.asList(columnNameProperty.split(","));
        columnTypes = TypeInfoUtils
                .getTypeInfosFromTypeString(columnTypeProperty);
        assert columnNames.size() == columnTypes.size();
        numColumns = columnNames.size();

        rowTypeInfo = TypeInfoFactory.getStructTypeInfo(columnNames, columnTypes);
        rowOI = (StructObjectInspector) TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(rowTypeInfo);
        row = new ArrayList<Object>(columnNames.size());
        for (int i = 0; i < columnNames.size(); i++) {
            row.add(null);
        }
        outputFields = new Object[numColumns];
        outputRowText = new Text();
    }

    @Override
    public Class<? extends Writable> getSerializedClass() {
        return Text.class;
    }

    private void setOutputField(int index, ObjectInspector fieldOI, Object field) {
        TypeInfo type = columnTypes.get(index);
        if (type.getTypeName().equals(Constants.STRING_TYPE_NAME)) {
            StringObjectInspector stringOI = (StringObjectInspector) fieldOI;
            outputFields[index] =  stringOI.getPrimitiveJavaObject(field);
        } else if (type.getTypeName().equals(Constants.INT_TYPE_NAME)) {
            IntObjectInspector intOI = (IntObjectInspector) fieldOI;
            outputFields[index] = intOI.getPrimitiveJavaObject(field);
        } else if (type.getTypeName().equals(Constants.SMALLINT_TYPE_NAME) ||
                type.getTypeName().equals(Constants.TINYINT_TYPE_NAME)) {
            ShortObjectInspector shortOI = (ShortObjectInspector) fieldOI;
            outputFields[index] = shortOI.getPrimitiveJavaObject(field);
        } else if (type.getTypeName().equals(Constants.BIGINT_TYPE_NAME)) {
            LongObjectInspector longOI = (LongObjectInspector) fieldOI;
            outputFields[index] = longOI.getPrimitiveJavaObject(field);
        } else if (type.getTypeName().equals(Constants.BOOLEAN_TYPE_NAME)) {
            BooleanObjectInspector booleanOI = (BooleanObjectInspector) fieldOI;
            outputFields[index] = booleanOI.getPrimitiveJavaObject(field);
        } else {
            StringObjectInspector stringOI = (StringObjectInspector) fieldOI;
            outputFields[index] =  stringOI.getPrimitiveJavaObject(field);            
        }
    }

    @Override
    public Writable serialize(Object o, ObjectInspector objectInspector) throws SerDeException {
        System.out.println("we are in serialize");
        StructObjectInspector outputRowOI = (StructObjectInspector) objectInspector;
        List<? extends StructField> outputFieldRefs = outputRowOI
                .getAllStructFieldRefs();
        if (outputFieldRefs.size() != numColumns) {
            throw new SerDeException("Cannot serialize the object because there are "
                    + outputFieldRefs.size() + " fields but the table has " + numColumns
                    + " columns.");
        }

        // Get all data out.
        for (int c = 0; c < numColumns; c++) {
            Object field = outputRowOI
                    .getStructFieldData(o, outputFieldRefs.get(c));
            ObjectInspector fieldOI = outputFieldRefs.get(c)
                    .getFieldObjectInspector();
            setOutputField(c, fieldOI, field);
        }

        // Format the String
        StringBuilder outputRowString = new StringBuilder();
        for (int c = 0; c < numColumns; ++c) {
            Object field = outputFields[c];
            outputRowString.append(String.format(inputLogFields.getFieldFormat(columnNames.get(c)), field));
        }
        outputRowText.set(outputRowString.toString());
        return outputRowText;
    }
    
    private String getFieldValue(String fieldName, String row) throws SerDeException {
        if (!inputLogFields.checkFieldExists(fieldName))
            throw new SerDeException("This table relates on an non-existent field "+fieldName);
        String regex = inputLogFields.getFieldPattern(fieldName);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(row);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }
            
    private void setTypedRowField(int index, String data) throws SerDeException {
        TypeInfo type = columnTypes.get(index);
        if (type.getTypeName().equals(Constants.STRING_TYPE_NAME)) {
            row.set(index, data);
        } else if (type.getTypeName().equals(Constants.INT_TYPE_NAME)) {
            try {
                Integer value = Integer.valueOf(data);
                row.set(index, value);
            } catch (NumberFormatException e) {
                row.set(index, null);
            }
        } else if (type.getTypeName().equals(Constants.SMALLINT_TYPE_NAME) ||
                type.getTypeName().equals(Constants.TINYINT_TYPE_NAME)) {
            row.set(index, new Short(data));
        } else if (type.getTypeName().equals(Constants.BIGINT_TYPE_NAME)) {
            row.set(index, new Long(data));
        } else if (type.getTypeName().equals(Constants.BOOLEAN_TYPE_NAME)) {
            row.set(index, new Boolean(data));
        } else if (type.getTypeName().equals(Constants.DATETIME_TYPE_NAME)) {
            row.set(index, data);
        } else if (type.getTypeName().equals(Constants.DATE_TYPE_NAME)) {
            row.set(index, data);
        } else if (type.getTypeName().equals(Constants.FLOAT_TYPE_NAME)) {
            throw new SerDeException("Float not supported");
        } else if (type.getTypeName().equals(Constants.DOUBLE_TYPE_NAME)) {
            throw new SerDeException("Double not supported");
        } else if (type.getTypeName().equals(Constants.TIMESTAMP_TYPE_NAME)) {
            row.set(index, data);
        }
        
    }

    @Override
    public Object deserialize(Writable writable) throws SerDeException {
        if (inputLogFields == null) {
            throw new SerDeException(
                    "This table does not have serde property \"input.log\"!");
        }
        Text rowText = (Text) writable;
        for (int i = 0; i < numColumns; i++) {
            String columnData = getFieldValue(columnNames.get(i), rowText.toString());
            if (columnData !=null) {
                setTypedRowField(i, columnData);
            }
            else {
                row.set(i, null);
            }
        }
        return row;
    }

    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return rowOI;
    }

    @Override
    public SerDeStats getSerDeStats() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
