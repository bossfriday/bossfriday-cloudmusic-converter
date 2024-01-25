package cn.bossfriday.cloudmusic.converter.entities;

import lombok.*;

/**
 * Rc4Key
 *
 * @author chenx
 */
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rc4Key {

    private String hardCodeSalt;

    private byte[] key;
}
