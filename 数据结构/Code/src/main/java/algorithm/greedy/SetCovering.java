package algorithm.greedy;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 贪心算法之集合覆盖问题
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/4/25/04/25  10:50
 */
public class SetCovering {

    public static void main(String[] args) {
        List<String> resList = new ArrayList();
        // 初始化地址数量
        List<String> address = new ArrayList();
        address.add("北京");
        address.add("上海");
        address.add("天津");
        address.add("广州");
        address.add("深圳");
        address.add("成都");
        address.add("大连");
        address.add("杭州");
        // 初始化电台及覆盖区域
        Map<String, String[]> radio = new HashMap<String, String[]>(5);
        radio.put("K1", new String[]{"北京", "上海", "天津"});
        radio.put("K2", new String[]{"北京", "广州", "深圳"});
        radio.put("K3", new String[]{"成都", "上海", "杭州"});
        radio.put("K4", new String[]{"上海", "天津"});
        radio.put("K5", new String[]{"杭州", "大连"});
        //当地址为空或者电台为空 结束
        while (address.size() > 0 && radio.size() > 0) {
            // 记录最大覆盖值
            AtomicInteger maxCovering = new AtomicInteger(0);
            // 记录最大覆盖值对应的 key
            AtomicReference<String> maxKey = new AtomicReference<>();
            // 遍历所有电台
            radio.forEach((k, v) -> {
                        // 1. 计算覆盖值
                        int cover = cover(v, address);
                        //覆盖为0 则电台无用
                        if (cover == 0) {
                            radio.remove(cover);
                        }
                        //比较寻找最大值覆盖值并记录
                        if (maxCovering.get() < cover) {
                            maxCovering.set(cover);
                            maxKey.set(k);
                        }
                    }
            );
            // 最大值的key 保存在结果集合中
            resList.add(maxKey.get());
            // 删除此电台对应的覆盖区域
            String[] strings = radio.get(maxKey.get());
            for (String string : strings) {
                address.remove(string);
            }
            // 删除此电台
            radio.remove(maxKey.get());
        }
        System.out.println(resList);
    }

    /**
     * 计算覆盖率
     *
     * @param v       电台区域
     * @param address 未覆盖区域
     * @return 电台覆盖了多少区域
     */
    private static int cover(String[] v, List<String> address) {
        int cover = 0;
        for (String s : v) {
            int i = address.indexOf(s);
            if (i != -1) {
                cover++;
            }
        }
        return cover;
    }
}
