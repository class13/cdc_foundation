package at.tailor.cdc.foundation.service.web.article.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/article")
class ArticleController {
    @PostMapping
    fun postArticle() {
        // todo: implement
    }

    @GetMapping
    fun getArticles() {
        // todo: implement
    }

    @PutMapping
    fun putArticle() {
        // todo: implement
    }

    @GetMapping
    fun getArticle() {
        // todo: implement
    }
}