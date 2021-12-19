package eu.devolios.zanibet.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnnotationJSONConverterFactory {

    public static final int JACKSON = 0;
    public static final int MOSHI = 1;
    public static final int GSON = 2;

    private int factory;

    public AnnotationJSONConverterFactory(@JSONConverterFactory int factory){
        this.factory = factory;
    }

    public int getFactory() {
        return factory;
    }

    @IntDef({ JACKSON, MOSHI, GSON })
    @Retention(RetentionPolicy.SOURCE)
    @interface JSONConverterFactory{
    }

}
