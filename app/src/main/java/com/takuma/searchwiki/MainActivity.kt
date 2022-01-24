package com.takuma.searchwiki

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search_text = findViewById<EditText>(R.id.keyword_text)
        val do_search = findViewById<Button>(R.id.do_search)
        val textview = findViewById<TextView>(R.id.extract_text)
        val title = findViewById<TextView>(R.id.title_text)

        do_search.setOnClickListener{
            val search:String = search_text.text.toString()
            val wiki_url:String = "https://ja.wikipedia.org/api/rest_v1/page/summary/$search"
            wikiTask(wiki_url)

        }
    }

    private fun wikiTask(wiki_url:String){
        GlobalScope.launch(Dispatchers.Main,CoroutineStart.DEFAULT){
            val result = wikiBackTask(wiki_url)

            wikiJsonTask(result)
        }
    }

    private suspend fun wikiBackTask(wiki_url: String):String{
        val response = withContext(Dispatchers.IO){
            var httpResult = ""

            try{
                val urlObj = URL(wiki_url)

                val br = BufferedReader(InputStreamReader(urlObj.openStream()))
                httpResult = br.readText()
            }catch (e:IOException
            ){
                e.printStackTrace()
            }catch (e:JSONException){
                e.printStackTrace()
            }
            return@withContext httpResult
        }
        return response
    }

    private fun wikiJsonTask(result:String){
        val extract_text = findViewById<TextView>(R.id.extract_text)
        val title = findViewById<TextView>(R.id.title_text)
        val jsonObj = JSONObject(result)

        val titleName = jsonObj.getString("title")
        title.text = titleName
        val extract = jsonObj.getString("extract")
        extract_text.text = extract
    }

}