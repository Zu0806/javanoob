package com.morris.mms.mms;

import java.util.List;
import java.util.Map;

public class OpenAiExtractors {
    @SuppressWarnings("unchecked")
    public static String outputText(Map<?, ?> resp) {
        // Responses API: resp.output[0].content[0].text 之類結構可能變動
        // 這裡用較保守的找法：遍歷 output->content 找 type=output_text 的 text
        Object outObj = resp.get("output");
        if (!(outObj instanceof List<?> outList)) return "";

        for (Object o : outList) {
            if (!(o instanceof Map<?, ?> om)) continue;
            Object contentObj = om.get("content");
            if (!(contentObj instanceof List<?> contentList)) continue;

            for (Object c : contentList) {
                if (!(c instanceof Map<?, ?> cm)) continue;
                Object type = cm.get("type");
                if ("output_text".equals(type)) {
                    Object text = cm.get("text");
                    return text == null ? "" : text.toString().trim();
                }
            }
        }
        return "";
    }
}
