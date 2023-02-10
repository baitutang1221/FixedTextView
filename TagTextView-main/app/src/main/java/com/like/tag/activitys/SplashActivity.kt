package com.like.tag.activitys

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.like.tag.R
import com.view.text.bean.BitmapPackageBean
import com.like.tag.views.TingTextViewClear
import com.like.tag.views.TingTextViewClearUpdate

class SplashActivity : AppCompatActivity() {

    private lateinit var image_tv1_1: TingTextViewClear
    private lateinit var image_tv1_2: TingTextViewClearUpdate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        image_tv1_1 = findViewById(R.id.image_tv1_1)
        image_tv1_2 = findViewById(R.id.image_tv1_2)

        val bitmap1 = BitmapFactory.decodeResource(resources, R.mipmap.icon_new3)
        val bitmap2 = BitmapFactory.decodeResource(resources, R.mipmap.icon_new1)
        val bitmap3 = BitmapFactory.decodeResource(resources, R.mipmap.custom_img)
        val bitmap4 = BitmapFactory.decodeResource(resources, R.mipmap.icon_3)
        val bitmap5 = BitmapFactory.decodeResource(resources, R.mipmap.img1)
        val bitmapPackageBean =
            BitmapPackageBean("img1", 100, 100, bitmap1)
        val bitmapPackageBean2 =
            BitmapPackageBean("img1", 100, 100, bitmap2)
        val bitmapPackageBean3 =
            BitmapPackageBean("img1", 100, 100, bitmap3)
        val bitmapPackageBean4 =
            BitmapPackageBean("img1", 100, 100, bitmap4)
        val bitmapPackageBean5 =
            BitmapPackageBean("img1", 100, 100, bitmap5)

        image_tv1_1.setContent(
            bitmapPackageBean,
            bitmapPackageBean,
            bitmapPackageBean,
            "1你好吗？你好吗？",
            bitmapPackageBean,
            "2这是一段不长不断",
            bitmapPackageBean5,
            "的话我要把他写下来",
            bitmapPackageBean3,
            bitmapPackageBean4,
            "3赵钱孙李周五正旺冯陈褚卫将神汉阳",
            bitmapPackageBean,
            "4hahhahahhfhahfhafhAAAAAAAAA",
            bitmapPackageBean5,
            bitmapPackageBean5,
            bitmapPackageBean5,
            "5首先，优先退税服务范围进一步扩大。在2021年度汇算对“上有老下有小”和看病负担较重的纳税人优先退税的基础上，进一步扩大优先退税服务范围，一是“下有小”的范围拓展至填报了3岁以下婴幼儿照护专项附加扣除的纳税人；二是将2022年度收入降幅较大的纳税人也纳入优先退税服务范围。",
            bitmapPackageBean2,
            bitmapPackageBean4,
            "6赵钱孙李周五正旺冯陈褚卫将神汉阳",
            bitmapPackageBean4,
            "7赵钱孙李周五正旺冯陈褚卫将神汉阳222",
            bitmapPackageBean5,
            bitmapPackageBean5,
            bitmapPackageBean5,
            bitmapPackageBean5
        )

        image_tv1_2.setContent(
            bitmapPackageBean,
            bitmapPackageBean,
            bitmapPackageBean,
            "1你好吗？你好吗？",
            bitmapPackageBean,
            "2这是一段不长不断",
            bitmapPackageBean5,
            "的话我要把他写下来",
            bitmapPackageBean3,
            bitmapPackageBean4,
            "3赵钱孙李周五正旺冯陈褚卫将神汉阳",
            bitmapPackageBean,
            "4hahhahahhfhahfhafhAAAAAAAAA",
            bitmapPackageBean5,
            bitmapPackageBean5,
            bitmapPackageBean5,
            "5首先，优先退税服务范围进一步扩大。在2021年度汇算对“上有老下有小”和看病负担较重的纳税人优先退税的基础上，进一步扩大优先退税服务范围，一是“下有小”的范围拓展至填报了3岁以下婴幼儿照护专项附加扣除的纳税人；二是将2022年度收入降幅较大的纳税人也纳入优先退税服务范围。",
            bitmapPackageBean2,
            bitmapPackageBean4,
            "6赵钱孙李周五正旺冯陈褚卫将神汉阳",
            bitmapPackageBean4,
            "7赵钱孙李周五正旺冯陈褚卫将神汉阳222",
            bitmapPackageBean5,
            bitmapPackageBean5,
            getString(R.string.tc_str1),
            bitmapPackageBean5,
            bitmapPackageBean5
        )

        findViewById<Button>(R.id.btn1).setOnClickListener {
            startActivity(Intent(this@SplashActivity, JavaActivity::class.java))
        }
    }
}