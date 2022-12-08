package com.example.webex;

import android.util.Log;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBHelper {

    private static final String host = "wt14.pgt.su/include/sql.php"; //ip адресс или домен сервера

    private OkHttpClient client;

    //При создании экземпляра класса создаётся независимый клиент для работы с сетью из библиотеки
    //OkHTTP
    public DBHelper() {
        client = new OkHttpClient();
    }

    //Пример процедуры авторизации.
    //принимает логин, пароль, событие в случае успеха, событие в случае неудачи
    public void authorize(String login, String password, Callable onSuccess, Callable onFail)
            throws UnsupportedEncodingException {

        //создём запрос к БД
        String query = "SELECT * FROM users WHERE login LIKE '"+login+"' " +
                "AND password LIKE '"+password+"';";

        //Получаем объект Observable который вернёт нам ответ от БД в виде строки
        Observable<String> queryExecutor = sendSelectQuery(query);

        //Добавляем этому объекту событие, что когда мы получим какой-то ответ от сервера,
        //мы выполним какие-то действия (внутри функции accept);
        sendSelectQuery(query).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                JSONArray results = new JSONArray(s);
                Log.e("QUERY", s);
                if (results.length() == 0) {
                    onFail.call();
                    return;
                }
                onSuccess.call();
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                throwable.printStackTrace();
            }
        }).subscribe();

        queryExecutor.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                throwable.printStackTrace();
            }
        });

        //Далее "подписываемся" на объект Observable, что заставит его выполнить запрос к серверу
        //и вернуть какой-то результат.
        queryExecutor.subscribe();
    }

    //Пример процедуры создания проекта
    //для простоты создадим проект с одним только названием.
    public void createProject(String title, Callable onSuccess, Callable onFail) {
        //Создаём запрос к БД.
        String query = "INSERT INTO projects VALUES (NULL, '"+title+"');";

        //Функция почти такая же как и sendSelectQuery, но записанная в коротком стиле и отличается
        //тем что ответ от сервера нам не нужен, но нам нужно лишь подтверждение что запрос дошёл и
        //был выполнен.
        sendInputQuery(query).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                //Если запрос дошёл до сервера и он его выполнил, на страницу будет выведена надпись OK
                //соответственно если это произошло, то вызываем событие "успеха" или если нет, то
                //событие "провала"
                if (s.equals("OK")) {
                    onSuccess.call();
                } else {
                    onFail.call();
                }
            }
        }).subscribe();
    }

    //Функция возвращает объект Observable
    //Observable.fromCallable создаёт Observable из функции которая возвращает какой-то результат
    //В нашем случае будет возвращён объект Response из библиотеки OkHTTP
    private Observable<String> sendSelectQuery(String query) throws UnsupportedEncodingException {
        return Observable.fromCallable(() -> {
                    //Возвращаем в Observable объект Response который получается в результате вызоыва
                    //у клиента (client) нового соединения (newCall), в которое передаётся объект класса Request,
                    //содержащий в себе URL страницы к которой нужно обратиться.
                    //execute() вызовет выполнение объекта newCall, т.е. отправит запрос на сервер.
                    //Когда сервер ответит на запрос, в поток Observable будет возвращён объект Response с этим ответом.
                    return client.newCall(new Request.Builder()
                            .url("http://"+host+"?type=select&query="
                                    +URLEncoder.encode(query, StandardCharsets.UTF_8.toString()))
                            .build()).execute();
                })
                .observeOn(AndroidSchedulers.mainThread()) //При обработке элемента в функции doOnNext будет использоватся главным поток приложения
                .subscribeOn(Schedulers.io())   //Сама стрелочная функция из fromCallable и map будут выполнены в отдельном потоке IO
                //Функция map преобразует элемент из одного типа в другой. В данном случае нам нужно получить текстовое содержимое
                //Страницы к которой мы обращаемся, поэтому мы преобразуем объект Response в String, воспользовавшись готовыми
                //методами объекта response
                .map(new Function<Response, String>() {
                    @Override
                    public String apply(Response response) throws Throwable {
                        return response.body().string();
                    }
                });
        //В результате мы вернём функцию которая "обещает" что после подписки на неё она вернёт результат в виде строки.
    }

    private Observable<String> sendInputQuery(String query) {
        //Эта функция отличается от sendSelectQuery только значением параметра type в url страницы.
        return Observable.fromCallable(() -> {
                    return client.newCall(new Request.Builder()
                            .url("http://"+host+"?type=insert&query="
                                    +URLEncoder.encode(query, StandardCharsets.UTF_8.toString()))
                            .build()).execute();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(new Function<Response, String>() {
                    @Override
                    public String apply(Response response) throws Throwable {
                        return response.body().string();
                    }
                });
    }

}