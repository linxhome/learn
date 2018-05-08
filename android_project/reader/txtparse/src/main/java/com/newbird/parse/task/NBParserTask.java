package com.newbird.parse.task;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.config.TextMeasureSize;
import com.newbird.parse.core.NBMeasure;
import com.newbird.parse.core.NBParserCore;
import com.newbird.parse.model.NBLine;
import com.newbird.parse.model.NBPage;
import com.newbird.parse.model.NBWord;

import java.util.ArrayList;
import java.util.List;

public class NBParserTask implements Runnable {

    NBParserCore.AsyncResp asyncResp;
    NBParserCore.ParseWorker parseWrapper;

    public NBParserTask(NBParserCore.ParseWorker parseWrapper) {
        this.parseWrapper = parseWrapper;
    }

    public NBParserTask(NBParserCore.AsyncResp asyncResp, NBParserCore.ParseWorker parseWrapper) {
        this.asyncResp = asyncResp;
        this.parseWrapper = parseWrapper;
    }

    @Override
    public void run() {
        if (asyncResp != null) {
            asyncResp.setPages(getData());
        }
    }

    /**
     * 分页任务的核心方法
     * @return
     */
    public List<NBPage> getData() {
        String oriStr = parseWrapper.getOriString();
        FontConfig config = parseWrapper.getConfig();
        NBMeasure measure = parseWrapper.getMeasure();
        int length = oriStr.length();
        int index = 0;

        //区分标点，文字和换行符,多个换行符要合并
        List<NBWord> words = new ArrayList<>();
        NBWord word = null;
        while (index < length) {
            char charactor = oriStr.charAt(index);

            //当前是换行，前一个也是换行，需要合并换行符
            if (index > 1 && isPunct(charactor) && isPunct(oriStr.charAt(index - 1))) {
                word.type = NBWord.PUNCT;
            } else {
                word = new NBWord();
                if (isPunct(charactor)) {
                    word.type = NBWord.PUNCT;
                } else if (isEnterLine(charactor)) {
                    word.type = NBWord.ENTER;
                } else {
                    word.type = NBWord.WORD;
                }
                word.position = index;
                words.add(word);
            }
            index++;
        }

        //文字分行
        int areaWidth = config.screenWidth - config.horizonGap * 2;
        int areaHeight = config.screenHeight - config.verticalGap * 2;
        int remainWid = areaWidth;
        NBLine newLine = new NBLine();
        List<NBLine> lines = new ArrayList<>();
        lines.add(newLine);
        for (NBWord word1 : words) {
            //超过宽度或者遇到换行符就要换行
            if (word == null) {
                continue;
            }
            char currentChar = oriStr.charAt(word.position);
            TextMeasureSize size = measure.getSize(oriStr, config, word1.position, word1.position + 1);
            word.meaWidth = size.width;
            if (isEnterLine(currentChar)) {
                newLine.append(word1);
                newLine = new NBLine();
                remainWid = areaWidth;
                lines.add(newLine);
            } else if (remainWid - size.width >= 0) {
                newLine.append(word1);
            } else {
                newLine = new NBLine();
                remainWid = areaWidth;
                newLine.append(word1);
                lines.add(newLine);
            }
        }

        int lineHeight = measure.getSize(oriStr, config, 0, 1).height;
        //标题额外空一行出来 ，标题字号自动加2
        //按行分页
        int linesPerPage = areaHeight / lineHeight;
        int linesSize = lines.size();
        List<NBPage> pages = new ArrayList<>();
        NBPage page = null;
        index = 0;
        while (index < linesSize) {
            if (index % linesPerPage == 0) {
                page = new NBPage();
                page.add(lines.get(index));
                page.config = config;
            } else {
                pages.add(page);
            }
            index++;
        }
        return pages;
    }

    /**
     * 判断是否标点符号
     *
     * @param cc
     * @return
     */
    private boolean isPunct(char cc) {
        String seperator = ",. 。，|-=+&^%$#@!&()?";
        return seperator.indexOf(cc) > 0;
    }

    private boolean isEnterLine(char cc) {
        String enterLine = "\r\n";
        return enterLine.indexOf(cc) > 0;
    }

}
