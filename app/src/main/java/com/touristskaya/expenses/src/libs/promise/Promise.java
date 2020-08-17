//package com.touristskaya.expenses.src.libs.promise;
//
//public class Promise<T> {
//    private Consumer<T> mResultAcceptor;
//    private Consumer<String> mErrorAcceptor;
//    private T mResult;
//    private String mError;
//
//    public Promise() {
//        mResultAcceptor = null;
//        mErrorAcceptor = null;
//        mResult = null;
//        mError = null;
//    }
//
//    public void resolve(T result) {
//        mResult = result;
//
//        if (mResultAcceptor != null) {
//            mResultAcceptor.accept(mResult);
//        }
//    }
//
//    public void reject(String error) {
//        mError = error;
//
//        if (mErrorAcceptor != null) {
//            mErrorAcceptor.accept(mError);
//        }
//    }
//
//    public void then(Consumer<T> resultAcceptor) {
//        mResultAcceptor = resultAcceptor;
//
//        if (mResult != null) {
//            mResultAcceptor.accept(mResult);
//        }
//    }
//
//    public void error(Consumer<String> errorAcceptor) {
//        mErrorAcceptor = errorAcceptor;
//
//        if (mError != null) {
//            mErrorAcceptor.accept(mError);
//        }
//    }
//}
