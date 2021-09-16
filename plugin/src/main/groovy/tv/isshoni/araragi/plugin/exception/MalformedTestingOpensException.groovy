package tv.isshoni.araragi.plugin.exception

class MalformedTestingOpensException extends RuntimeException {

    MalformedTestingOpensException(int size) {
        super("opens in the testing block is a pair list, please ensure pairs are supplied. (list size is odd: $size)")
    }
}
