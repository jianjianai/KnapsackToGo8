package cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi;

/**
 * @param ok true此库存存在，并已经准备好，可开始使用
 */
public record GetPairStorageC(
        boolean ok
){
}
