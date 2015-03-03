/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.api.question.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import solutions.doubts.core.api.question.domain.Question;
import solutions.doubts.core.api.question.retrofit.RetrofitQuestionService;

public class RestQuestionService {

    private RestAdapter restAdapter;

    public RestQuestionService(final RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public Question fetchQuestion(final int id,
                                  final String slug) {
        final RetrofitQuestionService retrofitQuestionService = this.restAdapter
                .create(RetrofitQuestionService.class);
        Question response = null;
        try {
            response = retrofitQuestionService.fetchQuestion(id, slug);
        } catch (RetrofitError error) {

        }
        return response;
    }

    public Question createNewQuestion(final Question question) {
        final RetrofitQuestionService retrofitQuestionService = this.restAdapter
                .create(RetrofitQuestionService.class);
        Question response = null;
        try {
            response = retrofitQuestionService.createNewQuestion(question);
        } catch (RetrofitError error) {
        }
        return response;
    }

    public Question updateQuestion(final int id,
                                   final String slug,
                                   final Question question) {
        final RetrofitQuestionService retrofitQuestionService = this.restAdapter
                .create(RetrofitQuestionService.class);
        Question response = null;
        try {
            response = retrofitQuestionService.updateQuestion(id, slug, question);
        } catch (RetrofitError error) {

        }
        return response;
    }

    public List<Question> fetchQuestionsForUser(final int authorId,
                                                final String authorUsername) {
        final RetrofitQuestionService retrofitQuestionService = this.restAdapter
                .create(RetrofitQuestionService.class);
        List<Question> questions = null;
        try {
            final Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("author.id", authorId);
            queryMap.put("author.username", authorUsername);
            final JsonObject responseJson = retrofitQuestionService.fetchQuestionResource(queryMap);
            questions = new Gson().fromJson(responseJson, new TypeToken<List<Question>>(){}.getType());
        } catch (RetrofitError error) {

        }
        return questions;
    }

    public List<Question> fetchQuestionsForTag(final List<String> tags) {
        final RetrofitQuestionService retrofitQuestionService = this.restAdapter
                .create(RetrofitQuestionService.class);
        List<Question> questions = null;
        try {
            final Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("questions.tags", tags);
            final JsonObject responseJson = retrofitQuestionService.fetchQuestionResource(queryMap);
            questions = new Gson().fromJson(responseJson, new TypeToken<List<Question>>(){}.getType());
        } catch (RetrofitError error) {

        }
        return questions;
    }

}
