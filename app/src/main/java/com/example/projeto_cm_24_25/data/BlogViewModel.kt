package com.example.projeto_cm_24_25.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.repository.BlogRepository
import kotlinx.coroutines.launch

class BlogViewModel: ViewModel() {
    val blogRepository : BlogRepository = BlogRepository()
    private val _blogData = MutableLiveData<List<Blog>>()
    val blogData : LiveData<List<Blog>> = _blogData

    fun getBlogData() {
        viewModelScope.launch {
            val blogResult = blogRepository.fetchBlogData()
            _blogData.value = blogResult
        }
    }

    fun addBlogData(blog: Blog) {
        blogRepository.addBlogData(blog)
    }
}