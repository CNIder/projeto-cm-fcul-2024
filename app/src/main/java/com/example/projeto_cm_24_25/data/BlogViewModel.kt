package com.example.projeto_cm_24_25.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.repository.BlogRepository
import kotlinx.coroutines.launch

class BlogViewModel: ViewModel() {
    private val blogRepository : BlogRepository = BlogRepository()
    private val _blogData = MutableLiveData<List<Blog>>()
    val blogData : LiveData<List<Blog>> = _blogData
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading : LiveData<Boolean> = _isLoading

    fun getBlogData() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val blogResult = blogRepository.fetchBlogData()
            _blogData.value = blogResult
            _isLoading.postValue(false)
        }
    }

    fun addBlogData(blog: Blog) {
        blogRepository.addBlogData(blog)
    }
}