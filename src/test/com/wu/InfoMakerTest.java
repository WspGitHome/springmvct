package com.wu;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.w.t.Application;
import com.w.t.dao.InstructionServiceDao;
import com.w.t.dao.ReportDao;
import com.w.t.dao.ReportServiceDao;
import com.w.t.dao.SlideShowServiceDao;
import com.w.t.entity.Instruction;
import com.w.t.entity.Report;
import com.w.t.entity.SlideShow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Packagename com.wu
 * @Classname InfoMakerTest
 * @Description
 * @Authors Mr.Wu
 * @Date 2022/06/01 09:00
 * @Version 1.0
 */
@SpringBootTest(classes = Application.class)
public class InfoMakerTest {

    @Autowired
    InstructionServiceDao instructionServiceDao;
    @Autowired
    ReportServiceDao reportServiceDao;
    @Autowired
    SlideShowServiceDao slideShowServiceDao;

    @Test
    public void 广东报告url快速生成() {
        String rootPath = "/Users/wushuping/Desktop/数据/数解广东报告/分析报告";
        String urlRoot = " http://19.15.81.243/pictures/数解广东报告/分析报告";
        List<File> files = FileUtil.loopFiles("/Users/wushuping/Desktop/数据/数解广东报告/分析报告");
        List<Report> result = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String[] split = files.get(i).getAbsolutePath().split("/");
            if (split.length == 10 && split[9].endsWith(".pdf")) {
                String title = split[9].substring(0, split[9].lastIndexOf(".pdf"));
                String url = files.get(i).getAbsolutePath().replace(rootPath, urlRoot);
                String quarter = quarterDeal(split[8]);
                String year = split[7];
                result.add(Report.builder().id(UUID.fastUUID().toString()).catagory("fxbg").quarter(quarter).createTime(DateUtil.date().toString()).year(year).url(url).title(title).build());
            }
        }
        reportServiceDao.saveBatch(result);
    }

    @Test
    public void 指示批文快速生成() {
        String rootPath = "/Users/wushuping/Desktop/数据/指示批文";
        String urlRoot = " http://19.15.81.243/pictures/指示批文";
        List<File> files = FileUtil.loopFiles(rootPath);

        List<Instruction> result = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String[] split = files.get(i).getAbsolutePath().split("/");
            if (split.length == 8) {
                String title = split[7].substring(0, split[7].lastIndexOf("."));
                String url = files.get(i).getAbsolutePath().replace(rootPath, urlRoot);
                String year = split[6];
                result.add(Instruction.builder().id(UUID.fastUUID().toString()).name(title).createTime(DateUtil.date().toString()).year(year).url(url).build());
            }
        }
        instructionServiceDao.saveBatch(result);
    }


    @Test
    public void 轮播图快速生成() {
        String rootPath = "/Users/wushuping/Desktop/数据/信心指数图片";
        String urlRoot = " http://19.15.81.243/pictures/信心指数图片/";
        List<File> files = FileUtil.loopFiles(rootPath);
        List<SlideShow> result = new ArrayList<>();
        for(int i=0;i<files.size();i++){

            File e = files.get(i);
            String fullPicName = e.getAbsolutePath().substring(e.getAbsolutePath().lastIndexOf("/") + 1);
            String name = fullPicName.substring(0, fullPicName.lastIndexOf("."));
            result.add(SlideShow.builder().name(name).url(urlRoot + fullPicName).id(UUID.fastUUID().toString()).sorted(i+1).build());
        }
        slideShowServiceDao.saveBatch(result);
    }

    private static String quarterDeal(String s) {
        if (s.trim().equals("一季度"))
            return "1";
        if (s.trim().equals("二季度"))
            return "2";
        if (s.trim().equals("三季度"))
            return "3";
        if (s.trim().equals("四季度"))
            return "4";
        return "0";
    }

    public static void main(String[] args) {
        String s = "abcd";
        System.out.println(s.lastIndexOf("a"));
    }
}
