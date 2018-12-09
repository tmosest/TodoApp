package com.tmosest.todoapp

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit var llTodos: LinearLayout
    private val result: Button by lazy { findViewById<Button>(R.id.btnAddTodo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("MainActivity", "onCreate called")
        findElements()
        result.text = "Add Todo"
        result.setOnClickListener { v ->
            var name = todoName.text
            var todo = Todo(name)
            val todoRow: LinearLayout = LinearLayout(this)
            val todoText = TextView(this)
            val todoButton = Button(this)
            todoButton.setOnClickListener { v ->
                todo.com = true
                todoText.setText("Name: " + todo.getName() + " is complete " + todo.com)
            }
            todoText.setText("Name: " + todo.getName() + " is complete " + todo.com)
            todoRow.addView(todoText)
            llTodos.addView(todoRow)
            fetchTodos()
        }
    }

    private fun findElements() {
        Log.i("MainActivity", "finding elements")
        llTodos = findViewById(R.id.llTodos)
    }

    private fun fetchTodos() {
        val todoDownloads = "http://example.com/todos"
        val downloadData = DownloadData()
        downloadData.execute(todoDownloads)
    }

    private class DownloadData : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String?): String {
            Log.d(tag, "doInBackground parameter is ${urls[0]}")
            val rssFeed = downloadXML(urls[0])
            if (rssFeed.isEmpty()) {
                Log.e(tag, "doInBackground: Error downloading")
            }
            return rssFeed
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d(tag, "onPostExecute parameter is $result")
        }

        private fun downloadXML(urlPath: String?): String {
            val xmlResult = StringBuilder()
            try {
                val url = URL(urlPath)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                val response = connection.responseCode
                Log.d(tag, "downloadXML: The response code was $response")
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                // From here down is rather weird to me...
                val inputBuffer = CharArray(500)
                var charsRead = 0
                while (charsRead >= 0) {
                    charsRead = reader.read(inputBuffer)
                    if (charsRead > 0) {
                        xmlResult.append(String(inputBuffer, 0, charsRead))
                    }
                }
                reader.close()
            } catch (exception: MalformedURLException) {
                Log.e(tag, "downloadXML: Invalid XML URL ${exception.message}")
            } catch (exception: IOException) {
                Log.e(tag, "downloadXML: IOException reading data ${exception.message}")
            } catch (exception: Exception) {
                Log.e(tag, "downloadXML: Unknown exception ${exception.message}")
            }
            return xmlResult.toString()
        }
    }
}

class Todo(name: String) {
    val n = name.toUpperCase()
    var com: Boolean = true

    init {
        Log.i("Todo", "init")
        com = false
    }

    fun getName(): String {
        return n
    }

    constructor(name: String, completed: Boolean): this(name) {
        com = completed
        Log.i("Todo", "dif constructor called")
    }
}

