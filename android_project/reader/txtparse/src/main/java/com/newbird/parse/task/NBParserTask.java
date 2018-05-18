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

    private NBParserCore.AsyncResp mAsyncResp;
    private NBParserCore.ParseWorker mParseWrapper;
    private int mStartReadPosition = 0;

    public NBParserTask(NBParserCore.ParseWorker parseWrapper) {
        this.mParseWrapper = parseWrapper;
    }

    public NBParserTask(NBParserCore.AsyncResp asyncResp, NBParserCore.ParseWorker parseWrapper) {
        this.mAsyncResp = asyncResp;
        this.mParseWrapper = parseWrapper;
    }

    public NBParserTask(NBParserCore.AsyncResp asyncResp, NBParserCore.ParseWorker parseWrapper, int startReadPosition) {
        this.mAsyncResp = asyncResp;
        this.mParseWrapper = parseWrapper;
        this.mStartReadPosition = startReadPosition;
    }

    @Override
    public void run() {
        if (mAsyncResp != null) {
            mAsyncResp.setPages(createPages());
        }
    }

    /**
     * @return 开始阅读的位置
     */
    public List<NBPage> createPages() {

        String oriStr = mParseWrapper.getOriString();
        int length = oriStr.length();
        FontConfig config = mParseWrapper.getConfig();
        NBMeasure measure = mParseWrapper.getMeasure();

        //正向解析
        int index = 0;
        List<NBWord> words = new ArrayList<>();
        NBWord word = null;
        //区分标点，文字和换行符,多个换行符要合并
        while (index < length) {
            char character = oriStr.charAt(index);
            if (index > 1 && isEnterLine(oriStr.charAt(index - 1))) {
                //新的一行
                if (isEnterLine(character)) {
                    //当前是换行，需要合并换行符
                    word.type = NBWord.NEWLINE;
                } else {
                    //当前不是换行符，则是段落头
                    word.isParagraphHead = true;
                }
            } else {
                word = new NBWord();
                if (isComma(character)) {
                    word.type = NBWord.COMMA;
                } else if (isEnterLine(character)) {
                    word.type = NBWord.NEWLINE;
                } else {
                    word.type = NBWord.WORD;
                }
                word.position = index;
                words.add(word);
            }
            index++;
        }

        //文字分行
        int spaceWidth = measure.getSize("空格", config, 0, 2).width;
        int areaWidth = config.contentWidth - 2 * config.horizonMargin;
        int areaHeight = config.contentHeight - 2 * config.verticalMargin;
        int remainWid = areaWidth;
        NBLine newLine = new NBLine();
        List<NBLine> lines = new ArrayList<>();
        lines.add(newLine);
        for (NBWord word1 : words) {
            //超过宽度或者遇到换行符就要换行
            if (word1 == null) {
                continue;
            }
            TextMeasureSize size = measure.getSize(oriStr, config, word1.position, word1.position + 1);
            word1.textWidth = word1.measureWidth = size.width;
            if (word1.isParagraphHead) {
                word1.measureWidth += size.width + spaceWidth;
            }

            if (isEnterLine(oriStr.charAt(word1.position))) {
                newLine.append(word1);
                newLine = new NBLine();
                remainWid = areaWidth;
                lines.add(newLine);
            } else if (remainWid - size.width >= 0) {
                newLine.append(word1);
                remainWid -= size.width;
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
        int linesPerPage = areaHeight / (lineHeight + config.lineGap);
        int linesSize = lines.size();
        List<NBPage> pages = new ArrayList<>();
        NBPage page = null;
        index = 0;
        while (index < linesSize) {
            if (index % linesPerPage == 0) {
                page = new NBPage();
                page.config = config;
                page.oriString = oriStr;
                pages.add(page);
            }
            page.add(lines.get(index));
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
    private boolean isComma(char cc) {
        String separator = ",. 。，|-=+&^%$#@!&()?";
        return separator.indexOf(cc) > 0;
    }

    private boolean isEnterLine(char cc) {
        String enterLine = "\r\n";
        return enterLine.indexOf(cc) > 0;
    }

}
