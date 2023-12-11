package com.timetableGuap.network.response.serializer

import com.timetableGuap.database.data.GroupDatabase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = GroupDatabase::class)
class GroupSerializer : KSerializer<GroupDatabase> {


    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GroupDatabase") {
        element<Int>("id")
        element<String>("groupName")
        element<Int>("groupId")
    }

    override fun serialize(encoder: Encoder, value: GroupDatabase) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.id)
            encodeStringElement(descriptor, 1, value.groupName)
            encodeIntElement(descriptor, 2, value.groupId)
        }
    }

    override fun deserialize(decoder: Decoder): GroupDatabase {
        return decoder.decodeStructure(descriptor) {
            var id: Int? = null
            var groupName: String? = null
            var groupId: Int? = null

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    DECODE_DONE -> break@loop

                    0 -> id = decodeIntElement(descriptor, 0)
                    1 -> groupName = decodeStringElement(descriptor, 1)
                    2 -> groupId = decodeIntElement(descriptor, 2)

                    else -> throw SerializationException("Unexpected index $index")
                }
            }

            GroupDatabase(
                requireNotNull(id),
                requireNotNull(groupName),
                requireNotNull(groupId),
            )
        }
    }
}