package ru.yandex.practicum.serialization.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();
    private static final Logger log = LoggerFactory.getLogger(GeneralAvroSerializer.class);

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<SpecificRecordBase> datumWriter = new SpecificDatumWriter<>(data.getSchema());
            BinaryEncoder binaryEncoder = encoderFactory.binaryEncoder(outputStream, null);
            datumWriter.write(data, binaryEncoder);
            binaryEncoder.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            String msg = "ОШИБКА сериализазиции, topic=" + topic;
            log.warn("msg={} e.stackTrace={}", msg, Arrays.asList(e.getStackTrace()));
            throw new SerializationException(msg, e);
        }
    }
}