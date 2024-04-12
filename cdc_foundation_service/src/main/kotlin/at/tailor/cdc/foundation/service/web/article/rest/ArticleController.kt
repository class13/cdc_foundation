package at.tailor.cdc.foundation.service.web.article.rest

import at.tailor.cdc.foundation.service.web.article.service.ArticleService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article")
class ArticleController(
    val articleService: ArticleService
) {
    @PostMapping
    fun postArticle(@RequestBody article: CreateArticleDTO): ArticleDTO {
        return articleService.createArticle(article.toServiceModel()).let{ ArticleDTO.of(it) }
    }

    @GetMapping
    fun getArticles(): List<ArticleDTO> {
        return articleService.getArticles().map { ArticleDTO.of(it) }
    }

    @PutMapping("/{id}")
    fun putArticle(@PathVariable("id") id: Long, @RequestBody article: UpdateArticleDTO): ArticleDTO {
        return articleService.updateArticle(article.toServiceModel(id)).let { ArticleDTO.of(it) }
    }

    @GetMapping("/{id}")
    fun getArticle(@PathVariable("id") id: Long): ArticleDTO {
        return articleService.getArticle(id).let { ArticleDTO.of(it) }
    }
}