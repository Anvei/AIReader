package org.anvei.aireader.activity

import android.os.Bundle
import org.anvei.aireader.databinding.ActivityReadBinding

class ReadActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityReadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

}