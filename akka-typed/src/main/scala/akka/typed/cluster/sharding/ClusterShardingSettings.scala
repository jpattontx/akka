/*
 * Copyright (C) 2017 Lightbend Inc. <http://www.lightbend.com/>
 */

package akka.typed.cluster.sharding

import akka.actor.NoSerializationVerificationNeeded
import akka.annotation.InternalApi
import akka.cluster.sharding.{ ClusterShardingSettings ⇒ UntypedShardingSettings }
import akka.cluster.singleton.{ ClusterSingletonManagerSettings ⇒ UntypedClusterSingletonManagerSettings }
import akka.typed.ActorSystem
import akka.typed.cluster.{ Cluster, ClusterSingletonManagerSettings }
import com.typesafe.config.Config

import scala.concurrent.duration.FiniteDuration

object ClusterShardingSettings {

  /** Scala API: Creates new cluster sharding settings object */
  def apply(system: ActorSystem[_]): ClusterShardingSettings =
    fromConfig(system.settings.config.getConfig("akka.cluster.sharding"))

  def fromConfig(config: Config): ClusterShardingSettings = {
    val untypedSettings = UntypedShardingSettings(config)
    fromUntypedSettings(untypedSettings)
  }

  /** Java API: Creates new cluster sharding settings object */
  def create(system: ActorSystem[_]): ClusterShardingSettings =
    apply(system)

  /** INTERNAL API: Indended only for internal use, it is not recommended to keep converting between the setting types */
  private[akka] def fromUntypedSettings(untypedSettings: UntypedShardingSettings): ClusterShardingSettings = {
    new ClusterShardingSettings(
      role = untypedSettings.role,
      rememberEntities = untypedSettings.rememberEntities,
      journalPluginId = untypedSettings.journalPluginId,
      snapshotPluginId = untypedSettings.snapshotPluginId,
      stateStoreMode = StateStoreMode.byName(untypedSettings.stateStoreMode),
      new TuningParameters(untypedSettings.tuningParameters),
      new ClusterSingletonManagerSettings(
        untypedSettings.coordinatorSingletonSettings.singletonName,
        untypedSettings.coordinatorSingletonSettings.role,
        untypedSettings.coordinatorSingletonSettings.removalMargin,
        untypedSettings.coordinatorSingletonSettings.handOverRetryInterval
      )
    )
  }

  /** INTERNAL API: Indended only for internal use, it is not recommended to keep converting between the setting types */
  private[akka] def toUntypedSettings(settings: ClusterShardingSettings): UntypedShardingSettings = {
    new UntypedShardingSettings(
      role = settings.role,
      rememberEntities = settings.rememberEntities,
      journalPluginId = settings.journalPluginId,
      snapshotPluginId = settings.snapshotPluginId,
      stateStoreMode = settings.stateStoreMode.name,
      new UntypedShardingSettings.TuningParameters(
        bufferSize = settings.tuningParameters.bufferSize,
        coordinatorFailureBackoff = settings.tuningParameters.coordinatorFailureBackoff,
        retryInterval = settings.tuningParameters.retryInterval,
        handOffTimeout = settings.tuningParameters.handOffTimeout,
        shardStartTimeout = settings.tuningParameters.shardStartTimeout,
        shardFailureBackoff = settings.tuningParameters.shardFailureBackoff,
        entityRestartBackoff = settings.tuningParameters.entityRestartBackoff,
        rebalanceInterval = settings.tuningParameters.rebalanceInterval,
        snapshotAfter = settings.tuningParameters.snapshotAfter,
        keepNrOfBatches = settings.tuningParameters.keepNrOfBatches,
        leastShardAllocationRebalanceThreshold = settings.tuningParameters.leastShardAllocationRebalanceThreshold, // TODO extract it a bit
        leastShardAllocationMaxSimultaneousRebalance = settings.tuningParameters.leastShardAllocationMaxSimultaneousRebalance,
        waitingForStateTimeout = settings.tuningParameters.waitingForStateTimeout,
        updatingStateTimeout = settings.tuningParameters.updatingStateTimeout,
        entityRecoveryStrategy = settings.tuningParameters.entityRecoveryStrategy,
        entityRecoveryConstantRateStrategyFrequency = settings.tuningParameters.entityRecoveryConstantRateStrategyFrequency,
        entityRecoveryConstantRateStrategyNumberOfEntities = settings.tuningParameters.entityRecoveryConstantRateStrategyNumberOfEntities
      ),
      new UntypedClusterSingletonManagerSettings(
        settings.coordinatorSingletonSettings.singletonName,
        settings.coordinatorSingletonSettings.role,
        settings.coordinatorSingletonSettings.removalMargin,
        settings.coordinatorSingletonSettings.handOverRetryInterval
      ))

  }

  private def roleOption(role: String): Option[String] =
    if (role == "" || role == null) None else Option(role)

  sealed trait StateStoreMode { def name: String }
  object StateStoreMode {
    def byName(name: String): StateStoreMode =
      if (name == StateStoreModePersistence.name) StateStoreModePersistence
      else if (name == StateStoreModeDData.name) StateStoreModeDData
      else throw new IllegalArgumentException("Not recognized StateStoreMode, only 'persistence' and 'ddata' are supported.")
  }
  final case object StateStoreModePersistence extends StateStoreMode { override def name = "persistence" }
  final case object StateStoreModeDData extends StateStoreMode { override def name = "ddata" }

  // generated using kaze-class
  final class TuningParameters private (
    val bufferSize:                                         Int,
    val coordinatorFailureBackoff:                          FiniteDuration,
    val entityRecoveryConstantRateStrategyFrequency:        FiniteDuration,
    val entityRecoveryConstantRateStrategyNumberOfEntities: Int,
    val entityRecoveryStrategy:                             String,
    val entityRestartBackoff:                               FiniteDuration,
    val handOffTimeout:                                     FiniteDuration,
    val keepNrOfBatches:                                    Int,
    val leastShardAllocationMaxSimultaneousRebalance:       Int,
    val leastShardAllocationRebalanceThreshold:             Int,
    val rebalanceInterval:                                  FiniteDuration,
    val retryInterval:                                      FiniteDuration,
    val shardFailureBackoff:                                FiniteDuration,
    val shardStartTimeout:                                  FiniteDuration,
    val snapshotAfter:                                      Int,
    val updatingStateTimeout:                               FiniteDuration,
    val waitingForStateTimeout:                             FiniteDuration) {

    def this(untyped: UntypedShardingSettings.TuningParameters) {
      this(
        bufferSize = untyped.bufferSize,
        coordinatorFailureBackoff = untyped.coordinatorFailureBackoff,
        retryInterval = untyped.retryInterval,
        handOffTimeout = untyped.handOffTimeout,
        shardStartTimeout = untyped.shardStartTimeout,
        shardFailureBackoff = untyped.shardFailureBackoff,
        entityRestartBackoff = untyped.entityRestartBackoff,
        rebalanceInterval = untyped.rebalanceInterval,
        snapshotAfter = untyped.snapshotAfter,
        keepNrOfBatches = untyped.keepNrOfBatches,
        leastShardAllocationRebalanceThreshold = untyped.leastShardAllocationRebalanceThreshold, // TODO extract it a bit
        leastShardAllocationMaxSimultaneousRebalance = untyped.leastShardAllocationMaxSimultaneousRebalance,
        waitingForStateTimeout = untyped.waitingForStateTimeout,
        updatingStateTimeout = untyped.updatingStateTimeout,
        entityRecoveryStrategy = untyped.entityRecoveryStrategy,
        entityRecoveryConstantRateStrategyFrequency = untyped.entityRecoveryConstantRateStrategyFrequency,
        entityRecoveryConstantRateStrategyNumberOfEntities = untyped.entityRecoveryConstantRateStrategyNumberOfEntities
      )

    }

    require(
      entityRecoveryStrategy == "all" || entityRecoveryStrategy == "constant",
      s"Unknown 'entity-recovery-strategy' [$entityRecoveryStrategy], valid values are 'all' or 'constant'")

    def withBufferSize(value: Int): TuningParameters = copy(bufferSize = value)
    def withCoordinatorFailureBackoff(value: FiniteDuration): TuningParameters = copy(coordinatorFailureBackoff = value)
    def withEntityRecoveryConstantRateStrategyFrequency(value: FiniteDuration): TuningParameters = copy(entityRecoveryConstantRateStrategyFrequency = value)
    def withEntityRecoveryConstantRateStrategyNumberOfEntities(value: Int): TuningParameters = copy(entityRecoveryConstantRateStrategyNumberOfEntities = value)
    def withEntityRecoveryStrategy(value: java.lang.String): TuningParameters = copy(entityRecoveryStrategy = value)
    def withEntityRestartBackoff(value: FiniteDuration): TuningParameters = copy(entityRestartBackoff = value)
    def withHandOffTimeout(value: FiniteDuration): TuningParameters = copy(handOffTimeout = value)
    def withKeepNrOfBatches(value: Int): TuningParameters = copy(keepNrOfBatches = value)
    def withLeastShardAllocationMaxSimultaneousRebalance(value: Int): TuningParameters = copy(leastShardAllocationMaxSimultaneousRebalance = value)
    def withLeastShardAllocationRebalanceThreshold(value: Int): TuningParameters = copy(leastShardAllocationRebalanceThreshold = value)
    def withRebalanceInterval(value: FiniteDuration): TuningParameters = copy(rebalanceInterval = value)
    def withRetryInterval(value: FiniteDuration): TuningParameters = copy(retryInterval = value)
    def withShardFailureBackoff(value: FiniteDuration): TuningParameters = copy(shardFailureBackoff = value)
    def withShardStartTimeout(value: FiniteDuration): TuningParameters = copy(shardStartTimeout = value)
    def withSnapshotAfter(value: Int): TuningParameters = copy(snapshotAfter = value)
    def withUpdatingStateTimeout(value: FiniteDuration): TuningParameters = copy(updatingStateTimeout = value)
    def withWaitingForStateTimeout(value: FiniteDuration): TuningParameters = copy(waitingForStateTimeout = value)

    private def copy(
      bufferSize:                                         Int              = bufferSize,
      coordinatorFailureBackoff:                          FiniteDuration   = coordinatorFailureBackoff,
      entityRecoveryConstantRateStrategyFrequency:        FiniteDuration   = entityRecoveryConstantRateStrategyFrequency,
      entityRecoveryConstantRateStrategyNumberOfEntities: Int              = entityRecoveryConstantRateStrategyNumberOfEntities,
      entityRecoveryStrategy:                             java.lang.String = entityRecoveryStrategy,
      entityRestartBackoff:                               FiniteDuration   = entityRestartBackoff,
      handOffTimeout:                                     FiniteDuration   = handOffTimeout,
      keepNrOfBatches:                                    Int              = keepNrOfBatches,
      leastShardAllocationMaxSimultaneousRebalance:       Int              = leastShardAllocationMaxSimultaneousRebalance,
      leastShardAllocationRebalanceThreshold:             Int              = leastShardAllocationRebalanceThreshold,
      rebalanceInterval:                                  FiniteDuration   = rebalanceInterval,
      retryInterval:                                      FiniteDuration   = retryInterval,
      shardFailureBackoff:                                FiniteDuration   = shardFailureBackoff,
      shardStartTimeout:                                  FiniteDuration   = shardStartTimeout,
      snapshotAfter:                                      Int              = snapshotAfter,
      updatingStateTimeout:                               FiniteDuration   = updatingStateTimeout,
      waitingForStateTimeout:                             FiniteDuration   = waitingForStateTimeout): TuningParameters = new TuningParameters(
      bufferSize = bufferSize,
      coordinatorFailureBackoff = coordinatorFailureBackoff,
      entityRecoveryConstantRateStrategyFrequency = entityRecoveryConstantRateStrategyFrequency,
      entityRecoveryConstantRateStrategyNumberOfEntities = entityRecoveryConstantRateStrategyNumberOfEntities,
      entityRecoveryStrategy = entityRecoveryStrategy,
      entityRestartBackoff = entityRestartBackoff,
      handOffTimeout = handOffTimeout,
      keepNrOfBatches = keepNrOfBatches,
      leastShardAllocationMaxSimultaneousRebalance = leastShardAllocationMaxSimultaneousRebalance,
      leastShardAllocationRebalanceThreshold = leastShardAllocationRebalanceThreshold,
      rebalanceInterval = rebalanceInterval,
      retryInterval = retryInterval,
      shardFailureBackoff = shardFailureBackoff,
      shardStartTimeout = shardStartTimeout,
      snapshotAfter = snapshotAfter,
      updatingStateTimeout = updatingStateTimeout,
      waitingForStateTimeout = waitingForStateTimeout)

    override def toString =
      s"""TuningParameters(${bufferSize},${coordinatorFailureBackoff},${entityRecoveryConstantRateStrategyFrequency},${entityRecoveryConstantRateStrategyNumberOfEntities},${entityRecoveryStrategy},${entityRestartBackoff},${handOffTimeout},${keepNrOfBatches},${leastShardAllocationMaxSimultaneousRebalance},${leastShardAllocationRebalanceThreshold},${rebalanceInterval},${retryInterval},${shardFailureBackoff},${shardStartTimeout},${snapshotAfter},${updatingStateTimeout},${waitingForStateTimeout})"""
  }
}

/**
 * @param role specifies that this entity type requires cluster nodes with a specific role.
 *   If the role is not specified all nodes in the cluster are used.
 * @param rememberEntities true if active entity actors shall be automatically restarted upon `Shard`
 *   restart. i.e. if the `Shard` is started on a different `ShardRegion` due to rebalance or crash.
 * @param journalPluginId Absolute path to the journal plugin configuration entity that is to
 *   be used for the internal persistence of ClusterSharding. If not defined the default
 *   journal plugin is used. Note that this is not related to persistence used by the entity
 *   actors.
 * @param snapshotPluginId Absolute path to the snapshot plugin configuration entity that is to
 *   be used for the internal persistence of ClusterSharding. If not defined the default
 *   snapshot plugin is used. Note that this is not related to persistence used by the entity
 *   actors.
 * @param tuningParameters additional tuning parameters, see descriptions in reference.conf
 */
final class ClusterShardingSettings(
  val role:                         Option[String],
  val rememberEntities:             Boolean,
  val journalPluginId:              String,
  val snapshotPluginId:             String,
  val stateStoreMode:               ClusterShardingSettings.StateStoreMode,
  val tuningParameters:             ClusterShardingSettings.TuningParameters,
  val coordinatorSingletonSettings: ClusterSingletonManagerSettings) extends NoSerializationVerificationNeeded {

  import akka.typed.cluster.sharding.ClusterShardingSettings.{ StateStoreModeDData, StateStoreModePersistence }
  require(
    stateStoreMode == StateStoreModePersistence || stateStoreMode == StateStoreModeDData,
    s"Unknown 'state-store-mode' [$stateStoreMode], " +
      s"valid values are '${StateStoreModeDData.name}' or '${StateStoreModePersistence.name}'")

  /** If true, this node should run the shard region, otherwise just a shard proxy should started on this node. */
  @InternalApi
  private[akka] def shouldHostShard(cluster: Cluster): Boolean =
    role.isEmpty || cluster.selfMember.roles(role.get)

  def withRole(role: String): ClusterShardingSettings = copy(role = ClusterShardingSettings.roleOption(role))

  def withRole(role: Option[String]): ClusterShardingSettings = copy(role = role)

  def withRememberEntities(rememberEntities: Boolean): ClusterShardingSettings =
    copy(rememberEntities = rememberEntities)

  def withJournalPluginId(journalPluginId: String): ClusterShardingSettings =
    copy(journalPluginId = journalPluginId)

  def withSnapshotPluginId(snapshotPluginId: String): ClusterShardingSettings =
    copy(snapshotPluginId = snapshotPluginId)

  def withTuningParameters(tuningParameters: ClusterShardingSettings.TuningParameters): ClusterShardingSettings =
    copy(tuningParameters = tuningParameters)

  def withStateStoreMode(stateStoreMode: ClusterShardingSettings.StateStoreMode): ClusterShardingSettings =
    copy(stateStoreMode = stateStoreMode)

  /**
   * The `role` of the `ClusterSingletonManagerSettings` is not used. The `role` of the
   * coordinator singleton will be the same as the `role` of `ClusterShardingSettings`.
   */
  def withCoordinatorSingletonSettings(coordinatorSingletonSettings: ClusterSingletonManagerSettings): ClusterShardingSettings =
    copy(coordinatorSingletonSettings = coordinatorSingletonSettings)

  private def copy(
    role:                         Option[String]                           = role,
    rememberEntities:             Boolean                                  = rememberEntities,
    journalPluginId:              String                                   = journalPluginId,
    snapshotPluginId:             String                                   = snapshotPluginId,
    stateStoreMode:               ClusterShardingSettings.StateStoreMode   = stateStoreMode,
    tuningParameters:             ClusterShardingSettings.TuningParameters = tuningParameters,
    coordinatorSingletonSettings: ClusterSingletonManagerSettings          = coordinatorSingletonSettings): ClusterShardingSettings =
    new ClusterShardingSettings(
      role,
      rememberEntities,
      journalPluginId,
      snapshotPluginId,
      stateStoreMode,
      tuningParameters,
      coordinatorSingletonSettings)
}