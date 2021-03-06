inherit kernel
require recipes-kernel/linux/linux-dtb.inc

DESCRIPTION = "Linux kernel for Freescale platforms"
SECTION = "kernel"
LICENSE = "GPLv2"

require recipes-kernel/linux/linux-qoriq-sdk.inc

SRC_URI = "git://git.freescale.com/ppc/sdk/linux.git;nobranch=1 \
"
SRCREV = "6619b8b55796cdf0cec04b66a71288edd3057229"
PV = "3.12"
PR = "${INC_PR}.1"

SCMVERSION ?= "y"

DEPENDS_append = " libgcc"
KERNEL_CC_append = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append = " ${TOOLCHAIN_OPTIONS}"

do_configure_prepend() {
	# copy desired defconfig so we pick it up for the real kernel_do_configure
	cp ${KERNEL_DEFCONFIG} ${B}/.config
    
	# add config fragments    
    	if [ -f "${DELTA_KERNEL_DEFCONFIG}" ]; then
        	${S}/scripts/kconfig/merge_config.sh -m .config ${DELTA_KERNEL_DEFCONFIG}
    	fi

	# append sdk version in kernel version if SDK_VERSION is defined
        if [ -n "${SDK_VERSION}" ]; then
                 echo "CONFIG_LOCALVERSION=\"-${SDK_VERSION}\"" >> ${S}/.config
        fi
    
        # Add GIT revision to the local version 
        if [ "${SCMVERSION}" = "y" ]; then
               head=`git rev-parse --verify --short HEAD 2> /dev/null`
               printf "%s%s" +g $head > ${S}/.scmversion
        fi
}
